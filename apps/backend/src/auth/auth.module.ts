import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { TokenService } from './jwt.service';
import { UserStoreService } from './user-store.service';
import { JwtAuthGuard } from './jwt.guard';

@Module({
  controllers: [AuthController],
  providers: [AuthService, TokenService, UserStoreService, JwtAuthGuard],
  exports: [AuthService, TokenService, UserStoreService, JwtAuthGuard],
})
export class AuthModule {}
