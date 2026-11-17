import { BadRequestException, Injectable } from '@nestjs/common';
import { WalletLedgerService } from './wallet-ledger.service';

export interface StreakInfo {
  currentDay: number; // 1 to 7
  rewardAmount: number;
  canClaim: boolean;
  nextClaimTime?: string;
}

export interface LeaderboardEntry {
  rank: number;
  username: string;
  avatarUrl?: string;
  totalWon: number;
  topMultiplier: string;
  gameTitle: string;
}

@Injectable()
export class RewardsService {
  private readonly streakRewards = [500, 750, 1000, 1250, 1500, 2000, 3000];
  private readonly userClaimRecords = new Map<
    string,
    { lastClaimDate: Date; currentStreak: number }
  >();

  constructor(private readonly ledger: WalletLedgerService) {}

  public getStreakStatus(userId: string): StreakInfo {
    const record = this.userClaimRecords.get(userId);
    if (!record) {
      return {
        currentDay: 1,
        rewardAmount: this.streakRewards[0],
        canClaim: true,
      };
    }

    const elapsedMs = Date.now() - record.lastClaimDate.getTime();
    const oneDayMs = 24 * 3600 * 1000;
    const canClaim = elapsedMs >= oneDayMs;

    // Streak broken if > 48 hours
    const currentDay =
      elapsedMs > 2 * oneDayMs ? 1 : Math.min(7, record.currentStreak + (canClaim ? 1 : 0));
    const rewardAmount = this.streakRewards[currentDay - 1];

    const nextClaimTime = canClaim
      ? undefined
      : new Date(record.lastClaimDate.getTime() + oneDayMs).toISOString();

    return {
      currentDay,
      rewardAmount,
      canClaim,
      nextClaimTime,
    };
  }

  public async claimDailyStreak(userId: string): Promise<{
    claimedAmount: number;
    newBalance: number;
    newStreakDay: number;
    transactionId: string;
  }> {
    const status = this.getStreakStatus(userId);
    if (!status.canClaim) {
      throw new BadRequestException(
        'Daily reward is on cooldown. Next claim available in ' + status.nextClaimTime,
      );
    }

    const claimKey = `DAILY-STREAK-${userId}-${new Date().toISOString().substring(0, 10)}`;
    const tx = await this.ledger.credit(
      userId,
      status.rewardAmount,
      'DAILY_REWARD',
      claimKey,
      `Day ${status.currentDay} Streak Bonus`,
    );

    this.userClaimRecords.set(userId, {
      lastClaimDate: new Date(),
      currentStreak: status.currentDay,
    });

    const newBalance = await this.ledger.getBalance(userId);

    return {
      claimedAmount: status.rewardAmount,
      newBalance,
      newStreakDay: status.currentDay,
      transactionId: tx.id,
    };
  }

  public getGlobalLeaderboard(): LeaderboardEntry[] {
    return [
      {
        rank: 1,
        username: 'StarPilot',
        totalWon: 845000,
        topMultiplier: '184.20x',
        gameTitle: 'Rocket Rescue',
      },
      {
        rank: 2,
        username: 'AbyssDiver',
        totalWon: 620000,
        topMultiplier: '92.50x',
        gameTitle: 'Deep Dive',
      },
      {
        rank: 3,
        username: 'NeonFrog',
        totalWon: 489000,
        topMultiplier: '64.00x',
        gameTitle: 'Frog Frenzy',
      },
      {
        rank: 4,
        username: 'MineSweeper',
        totalWon: 395000,
        topMultiplier: '42.10x',
        gameTitle: 'Mines Grid',
      },
      {
        rank: 5,
        username: 'CyberRoller',
        totalWon: 290000,
        topMultiplier: '33.00x',
        gameTitle: 'Cyber Dice',
      },
    ];
  }

  public getDailyLeaderboard(): LeaderboardEntry[] {
    return [
      {
        rank: 1,
        username: 'StarPilot',
        totalWon: 92400,
        topMultiplier: '48.50x',
        gameTitle: 'Rocket Rescue',
      },
      {
        rank: 2,
        username: 'NeonFrog',
        totalWon: 61200,
        topMultiplier: '28.00x',
        gameTitle: 'Frog Frenzy',
      },
      {
        rank: 3,
        username: 'GoldMiner',
        totalWon: 45000,
        topMultiplier: '18.40x',
        gameTitle: 'Mines Grid',
      },
    ];
  }
}
