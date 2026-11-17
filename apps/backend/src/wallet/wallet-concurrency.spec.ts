import { describe, it, beforeEach } from 'node:test';
import * as assert from 'node:assert';
import { WalletLedgerService } from './wallet-ledger.service';
import { UserStoreService } from '../auth/user-store.service';
import * as crypto from 'crypto';

describe('WalletLedgerService Concurrency & Stress Testing (Phase 11 QA)', () => {
  let ledgerService: WalletLedgerService;
  let userStore: UserStoreService;

  beforeEach(async () => {
    userStore = new UserStoreService();
    ledgerService = new WalletLedgerService(userStore);

    // Seed test user with 10,000 VCOIN initial balance
    await userStore.createUser('concurrent@casino.test', 'ConcurrentGamer', 'hashed_pass');
    // Override created user id for deterministic testing
    const created = await userStore.findByEmail('concurrent@casino.test');
    if (created) {
      await userStore.updateWalletBalance(created.id, 10000);
    }
  });

  it('should maintain strict atomic balance integrity under 50 simultaneous debit & credit operations', async () => {
    const user = await userStore.findByEmail('concurrent@casino.test');
    assert.ok(user);
    const initialBalance = await ledgerService.getBalance(user.id);
    assert.strictEqual(initialBalance, 10000);

    // Prepare 30 debits of 100 and 20 credits of 150
    // Net expected impact: -3000 + 3000 = 0 balance delta
    const ops: Promise<any>[] = [];

    for (let i = 0; i < 30; i++) {
      ops.push(
        ledgerService.debit(
          user.id,
          100,
          'BET_DEBIT',
          `idem-debit-${crypto.randomUUID()}`,
          `round-debit-${i}`,
        ),
      );
    }

    for (let j = 0; j < 20; j++) {
      ops.push(
        ledgerService.credit(
          user.id,
          150,
          'WIN_CREDIT',
          `idem-credit-${crypto.randomUUID()}`,
          `round-credit-${j}`,
        ),
      );
    }

    // Execute all 50 operations simultaneously
    const results = await Promise.all(ops);
    assert.strictEqual(results.length, 50);

    const finalBalance = await ledgerService.getBalance(user.id);
    assert.strictEqual(finalBalance, 10000);

    const history = ledgerService.getHistory(user.id, 100);
    assert.strictEqual(history.length, 50);
  });

  it('should prevent negative balance under competitive simultaneous over-debit race conditions', async () => {
    const user = await userStore.findByEmail('concurrent@casino.test');
    assert.ok(user);

    // Set balance to exactly 500
    await userStore.updateWalletBalance(user.id, 500);

    // Launch 10 simultaneous debit operations of 100 each
    // Exactly 5 should succeed (5 * 100 = 500) and 5 must be rejected
    const tasks = Array.from({ length: 10 }, (_, idx) =>
      ledgerService
        .debit(user.id, 100, 'BET_DEBIT', `race-idem-${idx}`, `race-${idx}`)
        .then(() => ({ success: true, idx }))
        .catch((err) => ({ success: false, idx, error: err.message })),
    );

    const results = await Promise.all(tasks);
    const successes = results.filter((r) => r.success);
    const failures = results.filter((r) => !r.success);

    assert.strictEqual(successes.length, 5);
    assert.strictEqual(failures.length, 5);

    const finalBal = await ledgerService.getBalance(user.id);
    assert.strictEqual(finalBal, 0); // Never drops below zero
  });

  it('should guarantee idempotency when identical concurrent requests strike simultaneously', async () => {
    const user = await userStore.findByEmail('concurrent@casino.test');
    assert.ok(user);
    await userStore.updateWalletBalance(user.id, 2000);

    const sharedIdempotencyKey = 'shared-idempotency-key-alpha';

    // 10 concurrent requests sharing the exact same idempotency key
    const duplicateOps = Array.from({ length: 10 }, () =>
      ledgerService.debit(user.id, 250, 'BET_DEBIT', sharedIdempotencyKey, 'shared-ref-1'),
    );

    const responses = await Promise.all(duplicateOps);

    // All responses must share the exact same transaction ID
    const firstTxId = responses[0].id;
    for (const res of responses) {
      assert.strictEqual(res.id, firstTxId);
      assert.strictEqual(res.newBalance, 1750);
    }

    // Balance must have been debited exactly ONCE
    const finalBalance = await ledgerService.getBalance(user.id);
    assert.strictEqual(finalBalance, 1750);
  });
});
