import { Module } from '@nestjs/common';
import { WalletLedgerService } from './wallet-ledger.service';
import { RewardsService } from './rewards.service';
import { WalletController, LeaderboardController } from './wallet.controller';
import { AuthModule } from '../auth/auth.module';

@Module({
  imports: [AuthModule],
  controllers: [WalletController, LeaderboardController],
  providers: [WalletLedgerService, RewardsService],
  exports: [WalletLedgerService, RewardsService],
})
export class WalletModule {}
