import { Module } from '@nestjs/common';
import { RngService } from './rng.service';
import { GameSessionService } from './game-session.service';
import { RngController } from './rng.controller';
import { AuthModule } from '../auth/auth.module';

@Module({
  imports: [AuthModule],
  controllers: [RngController],
  providers: [RngService, GameSessionService],
  exports: [RngService, GameSessionService],
})
export class RngModule {}
