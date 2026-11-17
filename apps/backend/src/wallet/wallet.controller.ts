import { Controller, Get, Post, Query, Req, UseGuards } from '@nestjs/common';
import { WalletLedgerService } from './wallet-ledger.service';
import { RewardsService } from './rewards.service';
import { JwtAuthGuard, AuthenticatedRequest } from '../auth/jwt.guard';

@Controller('wallet')
@UseGuards(JwtAuthGuard)
export class WalletController {
  constructor(
    private readonly ledger: WalletLedgerService,
    private readonly rewardsService: RewardsService,
  ) {}

  @Get()
  public async getWallet(@Req() req: AuthenticatedRequest) {
    const balance = await this.ledger.getBalance(req.user.sub);
    return {
      balance,
      currencyCode: 'VCOIN',
    };
  }

  @Get('transactions')
  public getTransactions(@Req() req: AuthenticatedRequest, @Query('limit') limit?: number) {
    return this.ledger.getHistory(req.user.sub, limit ? Number(limit) : 20);
  }

  @Get('rewards')
  public getRewards(@Req() req: AuthenticatedRequest) {
    return this.rewardsService.getStreakStatus(req.user.sub);
  }

  @Post('rewards/claim')
  public async claimDaily(@Req() req: AuthenticatedRequest) {
    return this.rewardsService.claimDailyStreak(req.user.sub);
  }
}

@Controller('leaderboards')
export class LeaderboardController {
  constructor(private readonly rewardsService: RewardsService) {}

  @Get('global')
  public getGlobal() {
    return this.rewardsService.getGlobalLeaderboard();
  }

  @Get('daily')
  public getDaily() {
    return this.rewardsService.getDailyLeaderboard();
  }
}
