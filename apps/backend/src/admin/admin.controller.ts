import { Body, Controller, Get, Param, Patch, Post, Query, Req, UseGuards } from '@nestjs/common';
import { AdminService } from './admin.service';
import { AdminRoleGuard } from './admin.guard';
import { AuthenticatedRequest } from '../auth/jwt.guard';

@Controller('admin')
@UseGuards(AdminRoleGuard)
export class AdminController {
  constructor(private readonly adminService: AdminService) {}

  @Get('overview')
  public getOverview() {
    return this.adminService.getOverviewMetrics();
  }

  @Get('players')
  public listPlayers(@Query('q') query?: string) {
    return this.adminService.listPlayers(query);
  }

  @Post('players/:userId/status')
  public setPlayerStatus(
    @Req() req: AuthenticatedRequest,
    @Param('userId') userId: string,
    @Body('status') status: 'ACTIVE' | 'SUSPENDED',
    @Body('reason') reason: string,
  ) {
    return this.adminService.setPlayerStatus(req.user.sub, userId, status, reason);
  }

  @Get('games')
  public listGames() {
    return this.adminService.listGames();
  }

  @Patch('games/:gameId/maintenance')
  public toggleMaintenance(
    @Req() req: AuthenticatedRequest,
    @Param('gameId') gameId: string,
    @Body('maintenance') maintenance: boolean,
  ) {
    return this.adminService.toggleGameMaintenance(req.user.sub, gameId, maintenance);
  }

  @Get('audit-logs')
  public getAuditLogs(@Query('limit') limit?: number) {
    return this.adminService.getAuditLogs(limit ? Number(limit) : 50);
  }

  @Get('support/tickets')
  public getTickets() {
    return this.adminService.listSupportTickets();
  }

  @Patch('support/tickets/:ticketId/resolve')
  public resolveTicket(
    @Req() req: AuthenticatedRequest,
    @Param('ticketId') ticketId: string,
    @Body('resolutionNote') resolutionNote: string,
  ) {
    return this.adminService.resolveSupportTicket(req.user.sub, ticketId, resolutionNote);
  }
}
