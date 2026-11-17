import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { RngService } from './rng.service';
import { UserStoreService } from '../auth/user-store.service';
import * as crypto from 'crypto';

export interface StoredGameSession {
  sessionId: string;
  gameId: string;
  userId: string;
  betAmount: number;
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  status: 'INITIALIZED' | 'ACTIVE' | 'RESOLVED' | 'CANCELLED';
  crashMultiplier?: number;
  payoutAmount: number;
  outcomeHash?: string;
  createdAt: Date;
  resolvedAt?: Date;
}

export interface SessionAuditDto {
  sessionId: string;
  gameId: string;
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  outcomeHash: string;
  multiplier: number;
  payoutAmount: number;
  isVerified: boolean;
  createdAt: string;
}

@Injectable()
export class GameSessionService {
  private readonly sessions = new Map<string, StoredGameSession>();
  private nonceCounter = 1;

  constructor(
    private readonly rngService: RngService,
    private readonly userStore: UserStoreService,
  ) {}

  public async createSession(
    userId: string,
    gameId: string,
    betAmount: number,
    clientSeed?: string,
  ): Promise<{
    sessionId: string;
    gameId: string;
    betAmount: number;
    serverSeedHash: string;
    clientSeed: string;
    nonce: number;
    status: string;
    createdAt: string;
  }> {
    if (betAmount <= 0) {
      throw new BadRequestException('Bet amount must be greater than zero.');
    }

    const wallet = await this.userStore.getWallet(userId);
    if (!wallet || wallet.balance < betAmount) {
      throw new BadRequestException('Insufficient virtual coin balance.');
    }

    // Deduct bet amount atomically
    await this.userStore.updateWalletBalance(userId, wallet.balance - betAmount);

    const commitment = this.rngService.generateServerSeed();
    const effectiveClientSeed = clientSeed || crypto.randomBytes(16).toString('hex');
    const nonce = this.nonceCounter++;
    const sessionId = crypto.randomUUID();
    const now = new Date();

    const session: StoredGameSession = {
      sessionId,
      gameId,
      userId,
      betAmount,
      serverSeed: commitment.serverSeed,
      serverSeedHash: commitment.serverSeedHash,
      clientSeed: effectiveClientSeed,
      nonce,
      status: 'ACTIVE',
      payoutAmount: 0,
      createdAt: now,
    };

    this.sessions.set(sessionId, session);

    return {
      sessionId,
      gameId,
      betAmount,
      serverSeedHash: commitment.serverSeedHash,
      clientSeed: effectiveClientSeed,
      nonce,
      status: session.status,
      createdAt: now.toISOString(),
    };
  }

  public async resolveSession(
    sessionId: string,
    achievedMultiplier: number,
  ): Promise<{
    sessionId: string;
    status: string;
    multiplier: number;
    payout: number;
    newBalance: number;
    serverSeed: string; // Plaintext revealed
    serverSeedHash: string;
    clientSeed: string;
    nonce: number;
    outcomeHash: string;
  }> {
    const session = this.sessions.get(sessionId);
    if (!session) throw new NotFoundException('Game session not found');
    if (session.status === 'RESOLVED') throw new BadRequestException('Session already resolved');

    const derived = this.rngService.deriveOutcome(
      session.serverSeed,
      session.clientSeed,
      session.nonce,
    );

    const payout = Math.floor(session.betAmount * achievedMultiplier);
    session.payoutAmount = payout;
    session.crashMultiplier = achievedMultiplier;
    session.status = 'RESOLVED';
    session.outcomeHash = derived.hash;
    session.resolvedAt = new Date();

    const wallet = await this.userStore.getWallet(session.userId);
    const newBalance = (wallet?.balance || 0) + payout;
    await this.userStore.updateWalletBalance(session.userId, newBalance);

    return {
      sessionId: session.sessionId,
      status: session.status,
      multiplier: achievedMultiplier,
      payout,
      newBalance,
      serverSeed: session.serverSeed,
      serverSeedHash: session.serverSeedHash,
      clientSeed: session.clientSeed,
      nonce: session.nonce,
      outcomeHash: derived.hash,
    };
  }

  public getSessionAudit(sessionId: string): SessionAuditDto {
    const session = this.sessions.get(sessionId);
    if (!session) throw new NotFoundException('Session audit not found');
    if (session.status !== 'RESOLVED') {
      throw new BadRequestException('Cannot reveal audit for active or unresolved session');
    }

    const verification = this.rngService.verifyOutcome(
      session.serverSeed,
      session.clientSeed,
      session.nonce,
      session.outcomeHash!,
    );

    return {
      sessionId: session.sessionId,
      gameId: session.gameId,
      serverSeed: session.serverSeed,
      serverSeedHash: session.serverSeedHash,
      clientSeed: session.clientSeed,
      nonce: session.nonce,
      outcomeHash: session.outcomeHash!,
      multiplier: session.crashMultiplier || 0,
      payoutAmount: session.payoutAmount,
      isVerified: verification.isValid,
      createdAt: session.createdAt.toISOString(),
    };
  }
}
