import { Body, Controller, Get, Param, Post, Req, UseGuards } from '@nestjs/common';
import { RocketRescueService } from './rocket-rescue.service';
import { JwtAuthGuard, AuthenticatedRequest } from '../../auth/jwt.guard';

@Controller('games/rocket-rescue')
@UseGuards(JwtAuthGuard)
export class RocketRescueController {
  constructor(private readonly rocketRescueService: RocketRescueService) {}

  @Post('round')
  public async startRound(
    @Req() req: AuthenticatedRequest,
    @Body('betAmount') betAmount: number,
    @Body('clientSeed') clientSeed?: string,
  ) {
    return this.rocketRescueService.startRound(req.user.sub, betAmount, clientSeed);
  }

  @Post('cashout')
  public async cashout(@Req() req: AuthenticatedRequest, @Body('roundId') roundId: string) {
    return this.rocketRescueService.cashout(roundId, req.user.sub);
  }

  @Get(':roundId')
  public getRound(@Param('roundId') roundId: string) {
    return this.rocketRescueService.getRoundState(roundId);
  }
}
