import { describe, it, beforeEach } from 'node:test';
import * as assert from 'node:assert';
import { AuthService } from './auth.service';
import { UserStoreService } from './user-store.service';
import { TokenService } from './jwt.service';
import { PlayersService } from '../players/players.service';

describe('AuthService & PlayersService (Phase 3 Integration)', () => {
  let userStore: UserStoreService;
  let tokenService: TokenService;
  let authService: AuthService;
  let playersService: PlayersService;

  beforeEach(() => {
    userStore = new UserStoreService();
    tokenService = new TokenService();
    authService = new AuthService(userStore, tokenService);
    playersService = new PlayersService(userStore);
  });

  it('should successfully register a player and allocate 1,000 virtual coins', async () => {
    const res = await authService.register('pilot@casino.test', 'StarPilot', 'SecretPass123!');

    assert.strictEqual(res.user.email, 'pilot@casino.test');
    assert.strictEqual(res.user.username, 'StarPilot');
    assert.ok(res.tokens.accessToken.length > 20);
    assert.ok(res.tokens.refreshToken.length > 20);

    const me = await playersService.getMe(res.user.id);
    assert.strictEqual(me.balance, 1000);
    assert.strictEqual(me.currencyCode, 'VCOIN');
  });

  it('should reject duplicate registration for existing email', async () => {
    await authService.register('dup@casino.test', 'UserOne', 'SecretPass123!');

    await assert.rejects(
      async () => {
        await authService.register('dup@casino.test', 'UserTwo', 'SecretPass123!');
      },
      (err: any) => err.status === 409,
    );
  });

  it('should authenticate user with valid credentials and reject invalid password', async () => {
    await authService.register('auth@casino.test', 'AuthUser', 'ValidPassword123!');

    const loginRes = await authService.login('auth@casino.test', 'ValidPassword123!');
    assert.strictEqual(loginRes.user.username, 'AuthUser');

    await assert.rejects(
      async () => {
        await authService.login('auth@casino.test', 'WrongPassword!');
      },
      (err: any) => err.status === 401,
    );
  });

  it('should rotate refresh tokens and detect token reuse attacks', async () => {
    const reg = await authService.register('rotate@casino.test', 'RotateUser', 'SecretPass123!');
    const initialRefresh = reg.tokens.refreshToken;

    // First rotation succeeds
    const rotated = await authService.refreshToken(initialRefresh);
    assert.ok(rotated.accessToken);
    assert.notStrictEqual(rotated.refreshToken, initialRefresh);

    // Replay attack with used token -> triggers reuse detection & revokes all sessions
    await assert.rejects(
      async () => {
        await authService.refreshToken(initialRefresh);
      },
      (err: any) => err.status === 401 && err.message.includes('reuse detected'),
    );
  });

  it('should update player profile and support responsible gaming self-exclusion', async () => {
    const reg = await authService.register('gamer@casino.test', 'GamerOne', 'SecretPass123!');

    const updated = await playersService.updateMe(reg.user.id, {
      displayName: 'CaptainGamer',
      dailyTimeLimitMinutes: 60,
      coolOffDays: 7,
    });

    assert.strictEqual(updated.displayName, 'CaptainGamer');
    assert.strictEqual(updated.dailyTimeLimitMinutes, 60);
    assert.ok(updated.coolOffUntil !== undefined);
  });

  it('should support permanent account deletion and revoke active sessions', async () => {
    const reg = await authService.register('delete@casino.test', 'DeleteMe', 'SecretPass123!');
    const delRes = await playersService.deleteMe(reg.user.id);
    assert.strictEqual(delRes.success, true);

    await assert.rejects(
      async () => {
        await authService.login('delete@casino.test', 'SecretPass123!');
      },
      (err: any) => err.status === 401,
    );
  });
});
