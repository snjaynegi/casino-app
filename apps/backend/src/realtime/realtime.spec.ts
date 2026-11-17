import { describe, it, beforeEach } from 'node:test';
import * as assert from 'node:assert';
import { RealtimeService } from './realtime.service';

describe('RealtimeService & Multiplayer Engine (Phase 9 Real-Time Features)', () => {
  let service: RealtimeService;

  beforeEach(() => {
    service = new RealtimeService();
  });

  it('should track player presence increments and decrements accurately', () => {
    assert.strictEqual(service.getOnlineCount(), 0);

    service.incrementPresence();
    service.incrementPresence();
    assert.strictEqual(service.getOnlineCount(), 2);

    service.decrementPresence();
    assert.strictEqual(service.getOnlineCount(), 1);
  });

  it('should broadcast and track live bets in multiplayer rooms', () => {
    const bet1 = service.registerLiveBet('StarPilot', 250);
    assert.strictEqual(bet1.username, 'StarPilot');
    assert.strictEqual(bet1.betAmount, 250);
    assert.strictEqual(bet1.status, 'BETTING');

    const cashout = service.registerLiveCashout('StarPilot', 3.5, 875);
    assert.ok(cashout);
    assert.strictEqual(cashout?.status, 'CASHED_OUT');
    assert.strictEqual(cashout?.payout, 875);

    const activeBets = service.getActiveBets();
    assert.strictEqual(activeBets.length, 1);
    assert.strictEqual(activeBets[0].status, 'CASHED_OUT');
  });

  it('should advance synchronized flight ticks and detect crash transition', () => {
    const tick1 = service.updateFlightTick(2.0, 5.0);
    assert.strictEqual(tick1.phase, 'FLYING');
    assert.ok(tick1.multiplier > 1.0);

    const tickCrashed = service.updateFlightTick(25.0, 2.5);
    assert.strictEqual(tickCrashed.phase, 'CRASHED');
    assert.strictEqual(tickCrashed.multiplier, 2.5);
  });
});
