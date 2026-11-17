import { describe, it, beforeEach } from 'node:test';
import * as assert from 'node:assert';
import { WalletLedgerService } from './wallet-ledger.service';
import { RewardsService } from './rewards.service';
import { UserStoreService } from '../auth/user-store.service';

describe('WalletLedgerService & RewardsService (Phase 8 Virtual Currency & Rewards)', () => {
  let userStore: UserStoreService;
  let ledger: WalletLedgerService;
  let rewards: RewardsService;
  let testUserId: string;

  beforeEach(async () => {
    userStore = new UserStoreService();
    ledger = new WalletLedgerService(userStore);
    rewards = new RewardsService(ledger);

    const user = await userStore.createUser('wallet@test.com', 'WalletTester', 'hash');
    testUserId = user.id;
  });

  it('should maintain strict balance integrity across multiple credits and debits', async () => {
    const initialBalance = await ledger.getBalance(testUserId);
    assert.strictEqual(initialBalance, 1000);

    // Debit 200 for bet
    const debitTx = await ledger.debit(testUserId, 200, 'BET_DEBIT', 'idemp-debit-1');
    assert.strictEqual(debitTx.previousBalance, 1000);
    assert.strictEqual(debitTx.newBalance, 800);

    // Credit 500 for win
    const creditTx = await ledger.credit(testUserId, 500, 'WIN_CREDIT', 'idemp-credit-1');
    assert.strictEqual(creditTx.previousBalance, 800);
    assert.strictEqual(creditTx.newBalance, 1300);

    const finalBalance = await ledger.getBalance(testUserId);
    assert.strictEqual(finalBalance, 1300);
  });

  it('should prevent negative balance and reject over-debiting', async () => {
    await assert.rejects(
      async () => {
        await ledger.debit(testUserId, 1001, 'BET_DEBIT', 'idemp-overdraft');
      },
      (err: any) => err.status === 400 && err.message.includes('Insufficient'),
    );
  });

  it('should guarantee idempotency on duplicate requests and prevent double operations', async () => {
    const tx1 = await ledger.credit(testUserId, 250, 'DAILY_REWARD', 'idemp-key-repeat');
    const tx2 = await ledger.credit(testUserId, 250, 'DAILY_REWARD', 'idemp-key-repeat');

    assert.strictEqual(tx1.id, tx2.id);
    const balance = await ledger.getBalance(testUserId);
    assert.strictEqual(balance, 1250); // Only credited once!
  });

  it('should progress 7-day daily streak reward and enforce cooldown', async () => {
    const status = rewards.getStreakStatus(testUserId);
    assert.strictEqual(status.currentDay, 1);
    assert.strictEqual(status.canClaim, true);
    assert.strictEqual(status.rewardAmount, 500);

    const claimRes = await rewards.claimDailyStreak(testUserId);
    assert.strictEqual(claimRes.claimedAmount, 500);
    assert.strictEqual(claimRes.newBalance, 1500);

    // Immediate second claim must be rejected due to cooldown
    await assert.rejects(
      async () => {
        await rewards.claimDailyStreak(testUserId);
      },
      (err: any) => err.status === 400 && err.message.includes('cooldown'),
    );
  });

  it('should return populated global and daily leaderboards', () => {
    const globalLeaders = rewards.getGlobalLeaderboard();
    assert.ok(globalLeaders.length >= 5);
    assert.strictEqual(globalLeaders[0].rank, 1);
    assert.ok(globalLeaders[0].totalWon > 0);

    const dailyLeaders = rewards.getDailyLeaderboard();
    assert.ok(dailyLeaders.length >= 3);
  });
});
