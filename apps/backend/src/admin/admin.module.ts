import { Module } from '@nestjs/common';
import { AdminService } from './admin.service';
import { AdminController } from './admin.controller';
import { AdminRoleGuard } from './admin.guard';
import { AuthModule } from '../auth/auth.module';
import { GamesModule } from '../games/games.module';
import { WalletModule } from '../wallet/wallet.module';

@Module({
  imports: [AuthModule, GamesModule, WalletModule],
  controllers: [AdminController],
  providers: [AdminService, AdminRoleGuard],
  exports: [AdminService, AdminRoleGuard],
})
export class AdminModule {}
