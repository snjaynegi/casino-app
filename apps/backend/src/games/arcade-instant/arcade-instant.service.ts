import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { RngService } from '../../rng/rng.service';
import { UserStoreService } from '../../auth/user-store.service';
import { MinesEngine } from './mines.engine';
import { PlinkoEngine, PlinkoDropResult } from './plinko.engine';
import { DiceEngine, DiceRollResult } from './dice.engine';

export interface ActiveMinesSession {
  sessionId: string;
  userId: string;
  betAmount: number;
  mineCount: number;
  minePositions: number[];
  revealedTiles: number[];
  status: 'ACTIVE' | 'WON' | 'LOST';
  currentMultiplier: number;
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
}

@Injectable()
export class ArcadeInstantService {
  private readonly minesSessions = new Map<string, ActiveMinesSession>();
  private nonce = 1000;

  constructor(
    private readonly rngService: RngService,
    private readonly userStore: UserStoreService,
  ) {}

  // --- MINES ---
  public async startMines(
    userId: string,
    betAmount: number,
    mineCount: number,
    clientSeed?: string,
  ) {
    if (mineCount < 1 || mineCount > 24) {
      throw new BadRequestException('Mines count must be between 1 and 24.');
    }
    const wallet = await this.userStore.getWallet(userId);
    if (!wallet || wallet.balance < betAmount) {
      throw new BadRequestException('Insufficient virtual coin balance.');
    }

    await this.userStore.updateWalletBalance(userId, wallet.balance - betAmount);

    const commitment = this.rngService.generateServerSeed();
    const effectiveClient = clientSeed || 'mines-client-seed';
    const roundNonce = this.nonce++;

    const minePositions = MinesEngine.generateMinePositions(
      this.rngService,
      commitment.serverSeed,
      effectiveClient,
      roundNonce,
      mineCount,
    );

    const sessionId = `MINES-${Date.now()}-${roundNonce}`;
    const session: ActiveMinesSession = {
      sessionId,
      userId,
      betAmount,
      mineCount,
      minePositions,
      revealedTiles: [],
      status: 'ACTIVE',
      currentMultiplier: 1.0,
      serverSeed: commitment.serverSeed,
      serverSeedHash: commitment.serverSeedHash,
      clientSeed: effectiveClient,
      nonce: roundNonce,
    };

    this.minesSessions.set(sessionId, session);

    return {
      sessionId,
      mineCount,
      betAmount,
      serverSeedHash: commitment.serverSeedHash,
      clientSeed: effectiveClient,
      nonce: roundNonce,
    };
  }

  public async revealMinesTile(sessionId: string, userId: string, tileIndex: number) {
    const session = this.minesSessions.get(sessionId);
    if (!session) throw new NotFoundException('Mines session not found');
    if (session.userId !== userId) throw new BadRequestException('Unauthorized session');
    if (session.status !== 'ACTIVE') throw new BadRequestException('Session already ended');
    if (tileIndex < 0 || tileIndex > 24)
      throw new BadRequestException('Tile index out of bounds [0-24]');
    if (session.revealedTiles.includes(tileIndex))
      throw new BadRequestException('Tile already revealed');

    session.revealedTiles.push(tileIndex);

    // Hit a mine?
    if (session.minePositions.includes(tileIndex)) {
      session.status = 'LOST';
      session.currentMultiplier = 0.0;
      return {
        sessionId,
        status: 'LOST',
        revealedTile: tileIndex,
        isMine: true,
        minePositions: session.minePositions, // reveal all
        multiplier: 0.0,
        payout: 0,
        serverSeed: session.serverSeed,
      };
    }

    // Uncovered Diamond
    const gemsRevealed = session.revealedTiles.length;
    const newMultiplier = MinesEngine.calculateMultiplier(session.mineCount, gemsRevealed);
    session.currentMultiplier = newMultiplier;

    return {
      sessionId,
      status: 'ACTIVE',
      revealedTile: tileIndex,
      isMine: false,
      gemsRevealed,
      currentMultiplier: newMultiplier,
      nextMultiplier: MinesEngine.calculateMultiplier(session.mineCount, gemsRevealed + 1),
    };
  }

  public async cashoutMines(sessionId: string, userId: string) {
    const session = this.minesSessions.get(sessionId);
    if (!session) throw new NotFoundException('Mines session not found');
    if (session.userId !== userId) throw new BadRequestException('Unauthorized');
    if (session.status !== 'ACTIVE') throw new BadRequestException('Session not active');
    if (session.revealedTiles.length === 0)
      throw new BadRequestException('Must reveal at least one tile');

    const payout = Math.floor(session.betAmount * session.currentMultiplier);
    session.status = 'WON';

    const wallet = await this.userStore.getWallet(userId);
    const newBalance = (wallet?.balance || 0) + payout;
    await this.userStore.updateWalletBalance(userId, newBalance);

    return {
      sessionId,
      status: 'WON',
      multiplier: session.currentMultiplier,
      payout,
      newBalance,
      minePositions: session.minePositions,
      serverSeed: session.serverSeed,
    };
  }

  // --- DICE ---
  public async rollDice(
    userId: string,
    betAmount: number,
    target: number,
    condition: 'OVER' | 'UNDER',
    clientSeed?: string,
  ): Promise<DiceRollResult & { payout: number; newBalance: number; serverSeed: string }> {
    if (target < 1.0 || target > 98.0) {
      throw new BadRequestException('Dice target must be between 1.00 and 98.00');
    }
    const wallet = await this.userStore.getWallet(userId);
    if (!wallet || wallet.balance < betAmount) {
      throw new BadRequestException('Insufficient balance');
    }

    await this.userStore.updateWalletBalance(userId, wallet.balance - betAmount);

    const commitment = this.rngService.generateServerSeed();
    const effectiveClient = clientSeed || 'dice-client';
    const roundNonce = this.nonce++;

    const result = DiceEngine.roll(
      this.rngService,
      commitment.serverSeed,
      effectiveClient,
      roundNonce,
      target,
      condition,
    );

    const payout = result.isWin ? Math.floor(betAmount * result.multiplier) : 0;
    const latestWallet = await this.userStore.getWallet(userId);
    const newBalance = (latestWallet?.balance || 0) + payout;
    await this.userStore.updateWalletBalance(userId, newBalance);

    return {
      ...result,
      payout,
      newBalance,
      serverSeed: commitment.serverSeed,
    };
  }

  // --- PLINKO ---
  public async dropPlinko(
    userId: string,
    betAmount: number,
    rows: number = 8,
    risk: 'LOW' | 'MEDIUM' | 'HIGH' = 'MEDIUM',
    clientSeed?: string,
  ): Promise<PlinkoDropResult & { payout: number; newBalance: number; serverSeed: string }> {
    const wallet = await this.userStore.getWallet(userId);
    if (!wallet || wallet.balance < betAmount) {
      throw new BadRequestException('Insufficient balance');
    }

    await this.userStore.updateWalletBalance(userId, wallet.balance - betAmount);

    const commitment = this.rngService.generateServerSeed();
    const effectiveClient = clientSeed || 'plinko-client';
    const roundNonce = this.nonce++;

    const result = PlinkoEngine.simulateDrop(
      this.rngService,
      commitment.serverSeed,
      effectiveClient,
      roundNonce,
      rows,
      risk,
    );

    const payout = Math.floor(betAmount * result.multiplier);
    const latestWallet = await this.userStore.getWallet(userId);
    const newBalance = (latestWallet?.balance || 0) + payout;
    await this.userStore.updateWalletBalance(userId, newBalance);

    return {
      ...result,
      payout,
      newBalance,
      serverSeed: commitment.serverSeed,
    };
  }
}
