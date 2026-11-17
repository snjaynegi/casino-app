import { Module } from '@nestjs/common';
import { HealthModule } from './health/health.module';
import { AuthModule } from './auth/auth.module';
import { PlayersModule } from './players/players.module';
import { GamesModule } from './games/games.module';
import { RngModule } from './rng/rng.module';
import { RocketRescueModule } from './games/rocket-rescue/rocket-rescue.module';
import { ArcadeInstantModule } from './games/arcade-instant/arcade-instant.module';
import { WalletModule } from './wallet/wallet.module';
import { RealtimeModule } from './realtime/realtime.module';
import { AdminModule } from './admin/admin.module';

@Module({
  imports: [
    HealthModule,
    AuthModule,
    PlayersModule,
    GamesModule,
    RngModule,
    RocketRescueModule,
    ArcadeInstantModule,
    WalletModule,
    RealtimeModule,
    AdminModule,
  ],
})
export class AppModule {}
