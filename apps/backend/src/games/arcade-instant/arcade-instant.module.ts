import { Module } from '@nestjs/common';
import { ArcadeInstantService } from './arcade-instant.service';
import { ArcadeInstantController } from './arcade-instant.controller';
import { RngModule } from '../../rng/rng.module';
import { AuthModule } from '../../auth/auth.module';

@Module({
  imports: [RngModule, AuthModule],
  controllers: [ArcadeInstantController],
  providers: [ArcadeInstantService],
  exports: [ArcadeInstantService],
})
export class ArcadeInstantModule {}
