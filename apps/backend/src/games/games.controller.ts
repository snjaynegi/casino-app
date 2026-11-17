import { Controller, Get, Param, Query } from '@nestjs/common';
import { GamesService, GameDetailDto } from './games.service';

@Controller('games')
export class GamesController {
  constructor(private readonly gamesService: GamesService) {}

  @Get()
  public list(@Query('category') category?: string, @Query('q') query?: string): GameDetailDto[] {
    return this.gamesService.getAllGames(category, query);
  }

  @Get(':gameId')
  public getOne(@Param('gameId') gameId: string): GameDetailDto {
    return this.gamesService.getGameById(gameId);
  }

  @Get(':gameId/config')
  public getConfig(@Param('gameId') gameId: string) {
    return this.gamesService.getGameConfig(gameId);
  }
}
