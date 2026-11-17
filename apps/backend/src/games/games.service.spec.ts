import { describe, it } from 'node:test';
import * as assert from 'node:assert';
import { GamesService } from './games.service';

describe('GamesService (Phase 4 Catalogue)', () => {
  const service = new GamesService();

  it('should return all approved games by default', () => {
    const games = service.getAllGames();
    assert.ok(games.length >= 8);
    const rocket = games.find((g) => g.id === 'rocket-rescue');
    assert.ok(rocket);
    assert.strictEqual(rocket.category, 'CRASH');
    assert.strictEqual(rocket.rtp, '97.0%');
  });

  it('should filter games by category correctly', () => {
    const crashGames = service.getAllGames('CRASH');
    assert.ok(crashGames.length > 0);
    assert.ok(crashGames.every((g) => g.category === 'CRASH'));

    const arcadeGames = service.getAllGames('ARCADE');
    assert.ok(arcadeGames.length >= 3);
    assert.ok(arcadeGames.every((g) => g.category === 'ARCADE'));
  });

  it('should search games by title and keyword', () => {
    const results = service.getAllGames(undefined, 'frog');
    assert.strictEqual(results.length, 1);
    assert.strictEqual(results[0].id, 'frog-frenzy');
  });

  it('should return detailed configuration with provable fairness parameters', () => {
    const config = service.getGameConfig('rocket-rescue');
    assert.strictEqual(config.gameId, 'rocket-rescue');
    assert.strictEqual(config.provablyFair, true);
    assert.strictEqual(config.currencyCode, 'VCOIN');
  });
});
