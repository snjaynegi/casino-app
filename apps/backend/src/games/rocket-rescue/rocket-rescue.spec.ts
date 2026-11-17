import { describe, it, beforeEach } from 'node:test';
import * as assert from 'node:assert';
import { RocketRescueService } from './rocket-rescue.service';
import { RocketRescueEngine } from './rocket-rescue.engine';
import { RngService } from '../../rng/rng.service';
import { UserStoreService } from '../../auth/user-store.service';

describe('RocketRescueEngine & Service (Phase 6 Premiere Game)', () => {
  let rngService: RngService;
  let userStore: UserStoreService;
  let service: RocketRescueService;
  let testUserId: string;

  beforeEach(async () => {
    rngService = new RngService();
    userStore = new UserStoreService();
    service = new RocketRescueService(rngService, userStore);

    const user = await userStore.createUser('pilot@test.com', 'PilotAce', 'hash');
    testUserId = user.id;
  });

  it('should compute continuous flight trajectory curve and invert crash duration correctly', () => {
    const mult0 = RocketRescueEngine.calculateMultiplierAtTime(0);
    assert.strictEqual(mult0, 1.0);

    const mult5 = RocketRescueEngine.calculateMultiplierAtTime(5);
    assert.ok(mult5 > 1.0);

    const duration = RocketRescueEngine.calculateCrashDuration(2.0);
    assert.ok(duration > 0);
    const recomputed = RocketRescueEngine.calculateMultiplierAtTime(duration);
    assert.ok(Math.abs(recomputed - 2.0) < 0.05);
  });

  it('should deduct bet from virtual wallet and commit server seed SHA-256 upon round start', async () => {
    const round = await service.startRound(testUserId, 200, 'client-custom-seed');

    assert.ok(round.roundId.startsWith('RR-'));
    assert.strictEqual(round.betAmount, 200);
    assert.strictEqual(round.serverSeedHash.length, 64);
    assert.strictEqual(round.newBalance, 800); // 1000 - 200
  });

  it('should successfully eject & cash out active flight before crash', async () => {
    const round = await service.startRound(testUserId, 100, 'client-test', 3.5);

    const cashoutRes = await service.cashout(round.roundId, testUserId);
    assert.ok(cashoutRes.multiplier >= 1.0);
    assert.ok(cashoutRes.payout >= 100);
    assert.strictEqual(cashoutRes.serverSeed.length, 64);

    const wallet = await userStore.getWallet(testUserId);
    assert.strictEqual(wallet?.balance, cashoutRes.newBalance);

    // Double cashout attempt must fail
    await assert.rejects(
      async () => {
        await service.cashout(round.roundId, testUserId);
      },
      (err: any) => err.status === 400,
    );
  });
});
