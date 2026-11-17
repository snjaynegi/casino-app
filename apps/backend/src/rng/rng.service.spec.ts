import { describe, it } from 'node:test';
import * as assert from 'node:assert';
import { RngService } from './rng.service';
import { GameSessionService } from './game-session.service';
import { UserStoreService } from '../auth/user-store.service';

describe('RngService & Provable Fairness Engine (Phase 5 Verification)', () => {
  const rngService = new RngService();

  it('should generate a 64-char hex server seed and valid SHA-256 pre-commitment hash', () => {
    const { serverSeed, serverSeedHash } = rngService.generateServerSeed();
    assert.strictEqual(serverSeed.length, 64);
    assert.strictEqual(serverSeedHash.length, 64);
  });

  it('should deterministically derive identical hashes given the same seeds and nonce', () => {
    const serverSeed = 'abc1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef';
    const clientSeed = 'custom-user-client-seed';
    const nonce = 42;

    const res1 = rngService.deriveOutcome(serverSeed, clientSeed, nonce);
    const res2 = rngService.deriveOutcome(serverSeed, clientSeed, nonce);

    assert.strictEqual(res1.hash, res2.hash);
    assert.strictEqual(res1.normalizedFloat, res2.normalizedFloat);
    assert.ok(res1.normalizedFloat >= 0 && res1.normalizedFloat < 1);
  });

  it('should pass Chi-Square Goodness-of-Fit distribution test over 10,000 outcomes', () => {
    const result = rngService.runChiSquareUniformityTest(10000, 10);
    assert.strictEqual(result.degreesOfFreedom, 9);
    assert.ok(result.passed, `Chi-square value ${result.chiSquare} must be < 21.666 (alpha=0.01)`);
  });

  it('should accurately verify an outcome via public verification API', () => {
    const { serverSeed } = rngService.generateServerSeed();
    const clientSeed = 'verify-seed';
    const nonce = 1;
    const derived = rngService.deriveOutcome(serverSeed, clientSeed, nonce);

    const verification = rngService.verifyOutcome(serverSeed, clientSeed, nonce, derived.hash);
    assert.strictEqual(verification.isValid, true);
    assert.strictEqual(verification.recomputedHash, derived.hash);

    const forgedVerification = rngService.verifyOutcome(
      serverSeed,
      clientSeed,
      nonce,
      'forged-hash',
    );
    assert.strictEqual(forgedVerification.isValid, false);
  });

  it('should manage atomic wallet balance updates in GameSessionService', async () => {
    const userStore = new UserStoreService();
    const user = await userStore.createUser('player@test.com', 'PlayerTest', 'hash');
    const sessionService = new GameSessionService(rngService, userStore);

    const walletBefore = await userStore.getWallet(user.id);
    assert.strictEqual(walletBefore?.balance, 1000);

    // Place bet 100
    const session = await sessionService.createSession(user.id, 'rocket-rescue', 100);
    const walletAfterBet = await userStore.getWallet(user.id);
    assert.strictEqual(walletAfterBet?.balance, 900);

    // Win at 2.50x
    const resolution = await sessionService.resolveSession(session.sessionId, 2.5);
    assert.strictEqual(resolution.payout, 250);
    assert.strictEqual(resolution.newBalance, 1150);

    // Verify audit
    const audit = sessionService.getSessionAudit(session.sessionId);
    assert.strictEqual(audit.isVerified, true);
    assert.strictEqual(audit.multiplier, 2.5);
  });
});
