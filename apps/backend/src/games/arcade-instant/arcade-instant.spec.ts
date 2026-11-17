import { describe, it, beforeEach } from 'node:test';
import * as assert from 'node:assert';
import { ArcadeInstantService } from './arcade-instant.service';
import { MinesEngine } from './mines.engine';
import { DiceEngine } from './dice.engine';
import { PlinkoEngine } from './plinko.engine';
import { RngService } from '../../rng/rng.service';
import { UserStoreService } from '../../auth/user-store.service';

describe('Additional Game Suite Engines & Service (Phase 7)', () => {
  let rngService: RngService;
  let userStore: UserStoreService;
  let service: ArcadeInstantService;
  let testUserId: string;

  beforeEach(async () => {
    rngService = new RngService();
    userStore = new UserStoreService();
    service = new ArcadeInstantService(rngService, userStore);

    const user = await userStore.createUser('gamer@test.com', 'ArcadeMaster', 'hash');
    testUserId = user.id;
  });

  it('MinesEngine: should generate exactly M unique mine positions within 0..24', () => {
    const minePositions = MinesEngine.generateMinePositions(rngService, 'seed-1', 'client-1', 1, 5);
    assert.strictEqual(minePositions.length, 5);
    const unique = new Set(minePositions);
    assert.strictEqual(unique.size, 5);
    assert.ok(minePositions.every((p) => p >= 0 && p < 25));
  });

  it('MinesEngine: multiplier increases strictly as gems are revealed', () => {
    const mult1 = MinesEngine.calculateMultiplier(3, 1);
    const mult2 = MinesEngine.calculateMultiplier(3, 2);
    const mult3 = MinesEngine.calculateMultiplier(3, 3);

    assert.ok(mult1 > 1.0);
    assert.ok(mult2 > mult1);
    assert.ok(mult3 > mult2);
  });

  it('DiceEngine: executes over/under probability check with 1.5% house edge', () => {
    const res = DiceEngine.roll(rngService, 'server-seed', 'client-seed', 1, 50.0, 'OVER');
    assert.ok(res.rolledNumber >= 0 && res.rolledNumber < 100);
    assert.strictEqual(res.winChance, 50.0);
    assert.strictEqual(res.multiplier, 1.97); // 98.5 / 50 = 1.97
    assert.strictEqual(res.isWin, res.rolledNumber > 50.0);
  });

  it('PlinkoEngine: simulates Galton board drop and maps to valid row payout slot', () => {
    const drop = PlinkoEngine.simulateDrop(
      rngService,
      'server-seed',
      'client-seed',
      1,
      8,
      'MEDIUM',
    );
    assert.strictEqual(drop.rows, 8);
    assert.strictEqual(drop.path.length, 8);
    assert.ok(drop.slotIndex >= 0 && drop.slotIndex <= 8);
    assert.ok(drop.multiplier > 0);
  });

  it('ArcadeInstantService: end-to-end mines round with cashout and balance update', async () => {
    const round = await service.startMines(testUserId, 100, 3);
    assert.strictEqual(round.mineCount, 3);

    // Find a non-mine tile
    const active = (service as any).minesSessions.get(round.sessionId);
    const safeTile = [0, 1, 2, 3, 4, 5].find((t) => !active.minePositions.includes(t))!;

    const revealRes = await service.revealMinesTile(round.sessionId, testUserId, safeTile);
    assert.strictEqual(revealRes.isMine, false);
    assert.ok((revealRes as any).currentMultiplier > 1.0);

    const cashoutRes = await service.cashoutMines(round.sessionId, testUserId);
    assert.strictEqual(cashoutRes.status, 'WON');
    assert.ok(cashoutRes.payout > 100);

    const wallet = await userStore.getWallet(testUserId);
    assert.strictEqual(wallet?.balance, cashoutRes.newBalance);
  });
});
