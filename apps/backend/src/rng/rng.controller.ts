import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { RngService } from './rng.service';
import { GameSessionService, SessionAuditDto } from './game-session.service';

@Controller('fairness')
export class RngController {
  constructor(
    private readonly rngService: RngService,
    private readonly gameSessionService: GameSessionService,
  ) {}

  @Post('verify')
  public verify(
    @Body('serverSeed') serverSeed: string,
    @Body('clientSeed') clientSeed: string,
    @Body('nonce') nonce: number,
    @Body('expectedHash') expectedHash: string,
  ) {
    return this.rngService.verifyOutcome(serverSeed, clientSeed, nonce, expectedHash);
  }

  @Get(':sessionId')
  public getAuditRecord(@Param('sessionId') sessionId: string): SessionAuditDto {
    return this.gameSessionService.getSessionAudit(sessionId);
  }
}
