import { Module } from '@nestjs/common';
import { RocketRescueService } from './rocket-rescue.service';
import { RocketRescueController } from './rocket-rescue.controller';
import { RngModule } from '../../rng/rng.module';
import { AuthModule } from '../../auth/auth.module';

@Module({
  imports: [RngModule, AuthModule],
  controllers: [RocketRescueController],
  providers: [RocketRescueService],
  exports: [RocketRescueService],
})
export class RocketRescueModule {}
