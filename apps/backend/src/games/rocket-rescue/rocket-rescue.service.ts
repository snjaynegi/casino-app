import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { RngService } from '../../rng/rng.service';
import { UserStoreService } from '../../auth/user-store.service';
import { RocketRescueEngine } from './rocket-rescue.engine';

export interface ActiveRocketRound {
  roundId: string;
  userId: string;
  betAmount: number;
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  crashMultiplier: number;
  crashDurationSeconds: number;
  startTime: number; // Date.now()
  status: 'FLYING' | 'CASHED_OUT' | 'CRASHED';
  cashedOutMultiplier?: number;
  payoutAmount: number;
  crystalBonusMultiplier?: number;
}

@Injectable()
export class RocketRescueService {
  private readonly activeRounds = new Map<string, ActiveRocketRound>();
  private roundNonce = 1;

  constructor(
    private readonly rngService: RngService,
    private readonly userStore: UserStoreService,
  ) {}

  public async startRound(
    userId: string,
    betAmount: number,
    clientSeed?: string,
    forcedCrashMultiplier?: number,
  ): Promise<{
    roundId: string;
    betAmount: number;
    serverSeedHash: string;
    clientSeed: string;
    nonce: number;
    startTime: number;
    newBalance: number;
  }> {
    if (betAmount < 10 || betAmount > 5000) {
      throw new BadRequestException('Bet amount must be between 10 and 5,000 VCOIN.');
    }

    const wallet = await this.userStore.getWallet(userId);
    if (!wallet || wallet.balance < betAmount) {
      throw new BadRequestException('Insufficient virtual coin balance.');
    }

    // Deduct bet immediately
    const updatedWallet = await this.userStore.updateWalletBalance(
      userId,
      wallet.balance - betAmount,
    );

    const commitment = this.rngService.generateServerSeed();
    const effectiveClientSeed = clientSeed || 'rocket-client-seed';
    const nonce = this.roundNonce++;

    const crashOutcome = RocketRescueEngine.generateCrashOutcome(
      commitment.serverSeed,
      effectiveClientSeed,
      nonce,
    );

    const effectiveCrash =
      forcedCrashMultiplier !== undefined ? forcedCrashMultiplier : crashOutcome.crashMultiplier;

    const crashDuration = RocketRescueEngine.calculateCrashDuration(effectiveCrash);
    const roundId = `RR-${Date.now()}-${nonce}`;

    const round: ActiveRocketRound = {
      roundId,
      userId,
      betAmount,
      serverSeed: commitment.serverSeed,
      serverSeedHash: commitment.serverSeedHash,
      clientSeed: effectiveClientSeed,
      nonce,
      crashMultiplier: effectiveCrash,
      crashDurationSeconds: crashDuration,
      startTime: Date.now(),
      status: 'FLYING',
      payoutAmount: 0,
    };

    this.activeRounds.set(roundId, round);

    return {
      roundId,
      betAmount,
      serverSeedHash: commitment.serverSeedHash,
      clientSeed: effectiveClientSeed,
      nonce,
      startTime: round.startTime,
      newBalance: updatedWallet.balance,
    };
  }

  public async cashout(
    roundId: string,
    userId: string,
  ): Promise<{
    roundId: string;
    multiplier: number;
    payout: number;
    newBalance: number;
    serverSeed: string;
    serverSeedHash: string;
    clientSeed: string;
    nonce: number;
  }> {
    const round = this.activeRounds.get(roundId);
    if (!round) throw new NotFoundException('Active round not found');
    if (round.userId !== userId) throw new BadRequestException('Unauthorized round access');
    if (round.status !== 'FLYING') {
      throw new BadRequestException(`Round is already resolved (${round.status})`);
    }

    const elapsedSeconds = (Date.now() - round.startTime) / 1000;

    // Check if round already crashed prior to cashout arrival
    if (elapsedSeconds >= round.crashDurationSeconds) {
      round.status = 'CRASHED';
      throw new BadRequestException(
        `Rocket crashed at ${round.crashMultiplier.toFixed(2)}x before ejection!`,
      );
    }

    const currentMultiplier = RocketRescueEngine.calculateMultiplierAtTime(elapsedSeconds);
    const payout = Math.floor(round.betAmount * currentMultiplier);

    round.status = 'CASHED_OUT';
    round.cashedOutMultiplier = currentMultiplier;
    round.payoutAmount = payout;

    const wallet = await this.userStore.getWallet(userId);
    const newBalance = (wallet?.balance || 0) + payout;
    await this.userStore.updateWalletBalance(userId, newBalance);

    return {
      roundId,
      multiplier: currentMultiplier,
      payout,
      newBalance,
      serverSeed: round.serverSeed,
      serverSeedHash: round.serverSeedHash,
      clientSeed: round.clientSeed,
      nonce: round.nonce,
    };
  }

  public getRoundState(roundId: string): ActiveRocketRound {
    const round = this.activeRounds.get(roundId);
    if (!round) throw new NotFoundException('Round not found');

    const elapsed = (Date.now() - round.startTime) / 1000;
    if (round.status === 'FLYING' && elapsed >= round.crashDurationSeconds) {
      round.status = 'CRASHED';
    }

    return round;
  }
}
