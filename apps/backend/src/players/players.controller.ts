import { Body, Controller, Delete, Get, Patch, Req, UseGuards } from '@nestjs/common';
import { PlayersService, PlayerMeDto } from './players.service';
import { JwtAuthGuard, AuthenticatedRequest } from '../auth/jwt.guard';

@Controller('players')
@UseGuards(JwtAuthGuard)
export class PlayersController {
  constructor(private readonly playersService: PlayersService) {}

  @Get('me')
  public async getMe(@Req() req: AuthenticatedRequest): Promise<PlayerMeDto> {
    return this.playersService.getMe(req.user.sub);
  }

  @Patch('me')
  public async updateMe(
    @Req() req: AuthenticatedRequest,
    @Body()
    body: {
      displayName?: string;
      avatarUrl?: string;
      dailyTimeLimitMinutes?: number;
      coolOffDays?: number;
    },
  ): Promise<PlayerMeDto> {
    return this.playersService.updateMe(req.user.sub, body);
  }

  @Delete('me')
  public async deleteMe(
    @Req() req: AuthenticatedRequest,
  ): Promise<{ success: boolean; message: string }> {
    return this.playersService.deleteMe(req.user.sub);
  }
}
