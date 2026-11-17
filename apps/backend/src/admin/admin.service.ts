import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { UserStoreService } from '../auth/user-store.service';
import { GamesService } from '../games/games.service';
import { WalletLedgerService } from '../wallet/wallet-ledger.service';
import * as crypto from 'crypto';

export interface AdminAuditEntry {
  id: string;
  adminId: string;
  action: string;
  targetEntity: string;
  targetId: string;
  details: Record<string, any>;
  timestamp: string;
}

export interface SupportTicket {
  id: string;
  userId: string;
  username: string;
  subject: string;
  category: 'BUG' | 'ACCOUNT' | 'GAMEPLAY' | 'FEEDBACK';
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';
  createdAt: string;
}

@Injectable()
export class AdminService {
  private readonly auditLogs: AdminAuditEntry[] = [];
  private readonly supportTickets: SupportTicket[] = [
    {
      id: 'TICK-101',
      userId: 'usr-1',
      username: 'NeonPilot',
      subject: 'Inquiry regarding streak reward reset',
      category: 'ACCOUNT',
      status: 'OPEN',
      createdAt: new Date(Date.now() - 3600000).toISOString(),
    },
    {
      id: 'TICK-102',
      userId: 'usr-2',
      username: 'StarPilot',
      subject: 'Question on Provably Fair verification tool',
      category: 'GAMEPLAY',
      status: 'RESOLVED',
      createdAt: new Date(Date.now() - 7200000).toISOString(),
    },
  ];

  constructor(
    private readonly userStore: UserStoreService,
    private readonly gamesService: GamesService,
    private readonly ledger: WalletLedgerService,
  ) {}

  public getOverviewMetrics() {
    return {
      totalRegisteredPlayers: 28490,
      activeSessions: 412,
      virtualCoinsInCirculation: '184.2M VCOIN',
      systemHealth: '100% Operational',
      unresolvedSupportTickets: this.supportTickets.filter((t) => t.status !== 'RESOLVED').length,
    };
  }

  public async listPlayers(query?: string) {
    // Return sample and stored players
    const sample = [
      {
        id: 'p-1',
        username: 'StarPilot',
        email: 'star@casino.test',
        status: 'ACTIVE',
        level: 14,
        balance: 845000,
      },
      {
        id: 'p-2',
        username: 'AbyssDiver',
        email: 'diver@casino.test',
        status: 'ACTIVE',
        level: 9,
        balance: 620000,
      },
      {
        id: 'p-3',
        username: 'NeonFrog',
        email: 'frog@casino.test',
        status: 'ACTIVE',
        level: 6,
        balance: 489000,
      },
      {
        id: 'p-4',
        username: 'RuleBreaker',
        email: 'bad@actor.test',
        status: 'SUSPENDED',
        level: 1,
        balance: 50,
      },
    ];

    for (const p of sample) {
      const liveBal = await this.ledger.getBalance(p.id);
      if (liveBal > 0) p.balance = liveBal;
    }

    if (query && query.trim()) {
      const q = query.toLowerCase().trim();
      return sample.filter(
        (p) => p.username.toLowerCase().includes(q) || p.email.toLowerCase().includes(q),
      );
    }
    return sample;
  }

  public async setPlayerStatus(
    adminId: string,
    userId: string,
    newStatus: 'ACTIVE' | 'SUSPENDED',
    reason: string,
  ) {
    if (!reason || reason.trim().length < 5) {
      throw new BadRequestException(
        'A legitimate policy violation reason is mandatory for suspension.',
      );
    }

    const existingUser = await this.userStore.findById(userId);
    if (existingUser) {
      existingUser.status = newStatus;
    }

    const logEntry: AdminAuditEntry = {
      id: crypto.randomUUID(),
      adminId,
      action: newStatus === 'SUSPENDED' ? 'SUSPEND_PLAYER' : 'REACTIVATE_PLAYER',
      targetEntity: 'USER',
      targetId: userId,
      details: { newStatus, reason },
      timestamp: new Date().toISOString(),
    };

    this.auditLogs.unshift(logEntry);

    return {
      success: true,
      userId,
      newStatus,
      auditId: logEntry.id,
    };
  }

  public listGames() {
    return this.gamesService.getAllGames().map((g) => ({
      ...g,
      maintenanceMode: false,
    }));
  }

  public toggleGameMaintenance(adminId: string, gameId: string, maintenance: boolean) {
    const game = this.gamesService.getGameById(gameId);

    const logEntry: AdminAuditEntry = {
      id: crypto.randomUUID(),
      adminId,
      action: maintenance ? 'ENABLE_MAINTENANCE' : 'DISABLE_MAINTENANCE',
      targetEntity: 'GAME',
      targetId: gameId,
      details: { gameTitle: game.title, maintenance },
      timestamp: new Date().toISOString(),
    };

    this.auditLogs.unshift(logEntry);

    return {
      gameId,
      maintenance,
      auditId: logEntry.id,
    };
  }

  public getAuditLogs(limit: number = 50): AdminAuditEntry[] {
    return this.auditLogs.slice(0, limit);
  }

  public listSupportTickets(): SupportTicket[] {
    return this.supportTickets;
  }

  public resolveSupportTicket(adminId: string, ticketId: string, resolutionNote: string) {
    const ticket = this.supportTickets.find((t) => t.id === ticketId);
    if (!ticket) throw new NotFoundException('Support ticket not found');

    ticket.status = 'RESOLVED';

    const log: AdminAuditEntry = {
      id: crypto.randomUUID(),
      adminId,
      action: 'RESOLVE_TICKET',
      targetEntity: 'SUPPORT_TICKET',
      targetId: ticketId,
      details: { resolutionNote },
      timestamp: new Date().toISOString(),
    };
    this.auditLogs.unshift(log);

    return { success: true, ticket };
  }
}
