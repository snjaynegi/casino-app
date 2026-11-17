import { describe, it, beforeEach } from 'node:test';
import * as assert from 'node:assert';
import { AdminService } from './admin.service';
import { UserStoreService } from '../auth/user-store.service';
import { GamesService } from '../games/games.service';
import { WalletLedgerService } from '../wallet/wallet-ledger.service';

describe('AdminService (Phase 10 Governance & Auditing)', () => {
  let adminService: AdminService;
  let userStore: UserStoreService;
  let gamesService: GamesService;
  let ledger: WalletLedgerService;

  beforeEach(() => {
    userStore = new UserStoreService();
    gamesService = new GamesService();
    ledger = new WalletLedgerService(userStore);
    adminService = new AdminService(userStore, gamesService, ledger);
  });

  it('should return operational overview metrics and system health', () => {
    const overview = adminService.getOverviewMetrics();
    assert.strictEqual(overview.systemHealth, '100% Operational');
    assert.ok(overview.totalRegisteredPlayers > 0);
    assert.ok(overview.activeSessions > 0);
  });

  it('should list and search players', async () => {
    const players = await adminService.listPlayers();
    assert.ok(players.length >= 4);

    const searchRes = await adminService.listPlayers('StarPilot');
    assert.strictEqual(searchRes.length, 1);
    assert.strictEqual(searchRes[0].username, 'StarPilot');
  });

  it('should suspend player for policy violation and record immutable audit entry', async () => {
    const res = await adminService.setPlayerStatus(
      'admin-super-1',
      'p-1',
      'SUSPENDED',
      'Attempting automated botting script on game rounds',
    );

    assert.strictEqual(res.success, true);
    assert.strictEqual(res.newStatus, 'SUSPENDED');

    const logs = adminService.getAuditLogs();
    assert.strictEqual(logs.length, 1);
    assert.strictEqual(logs[0].action, 'SUSPEND_PLAYER');
    assert.strictEqual(logs[0].targetId, 'p-1');
  });

  it('should reject suspension without legitimate reason', async () => {
    await assert.rejects(
      async () => {
        await adminService.setPlayerStatus('admin-1', 'p-1', 'SUSPENDED', '');
      },
      (err: any) => err.status === 400,
    );
  });

  it('should toggle game maintenance and record audit log', async () => {
    const res = await adminService.toggleGameMaintenance('admin-1', 'rocket-rescue', true);
    assert.strictEqual(res.gameId, 'rocket-rescue');
    assert.strictEqual(res.maintenance, true);

    const logs = adminService.getAuditLogs();
    assert.ok(
      logs.some((l) => l.action === 'ENABLE_MAINTENANCE' && l.targetId === 'rocket-rescue'),
    );
  });

  it('should resolve customer support tickets and log administrative resolution', () => {
    const tickets = adminService.listSupportTickets();
    assert.ok(tickets.length >= 2);

    const res = adminService.resolveSupportTicket(
      'admin-support-1',
      'TICK-101',
      'Explained that 24h streak window expired after 48h idle period.',
    );

    assert.strictEqual(res.success, true);
    assert.strictEqual(res.ticket.status, 'RESOLVED');
  });
});
