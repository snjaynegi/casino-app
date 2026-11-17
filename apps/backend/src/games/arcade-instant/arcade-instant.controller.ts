import { Body, Controller, Post, Req, UseGuards } from '@nestjs/common';
import { ArcadeInstantService } from './arcade-instant.service';
import { JwtAuthGuard, AuthenticatedRequest } from '../../auth/jwt.guard';

@Controller('games')
@UseGuards(JwtAuthGuard)
export class ArcadeInstantController {
  constructor(private readonly service: ArcadeInstantService) {}

  @Post('mines/round')
  public async startMines(
    @Req() req: AuthenticatedRequest,
    @Body('betAmount') betAmount: number,
    @Body('mineCount') mineCount: number,
    @Body('clientSeed') clientSeed?: string,
  ) {
    return this.service.startMines(req.user.sub, betAmount, mineCount, clientSeed);
  }

  @Post('mines/reveal')
  public async revealTile(
    @Req() req: AuthenticatedRequest,
    @Body('sessionId') sessionId: string,
    @Body('tileIndex') tileIndex: number,
  ) {
    return this.service.revealMinesTile(sessionId, req.user.sub, tileIndex);
  }

  @Post('mines/cashout')
  public async cashoutMines(
    @Req() req: AuthenticatedRequest,
    @Body('sessionId') sessionId: string,
  ) {
    return this.service.cashoutMines(sessionId, req.user.sub);
  }

  @Post('dice/roll')
  public async rollDice(
    @Req() req: AuthenticatedRequest,
    @Body('betAmount') betAmount: number,
    @Body('target') target: number,
    @Body('condition') condition: 'OVER' | 'UNDER',
    @Body('clientSeed') clientSeed?: string,
  ) {
    return this.service.rollDice(req.user.sub, betAmount, target, condition, clientSeed);
  }

  @Post('plinko/drop')
  public async dropPlinko(
    @Req() req: AuthenticatedRequest,
    @Body('betAmount') betAmount: number,
    @Body('rows') rows: number,
    @Body('risk') risk: 'LOW' | 'MEDIUM' | 'HIGH',
    @Body('clientSeed') clientSeed?: string,
  ) {
    return this.service.dropPlinko(req.user.sub, betAmount, rows, risk, clientSeed);
  }
}
