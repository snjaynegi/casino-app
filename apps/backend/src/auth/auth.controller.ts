import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { AuthService } from './auth.service';
import { LoginRequestDto, RefreshTokenRequestDto, RegisterRequestDto } from '@casino/api-contracts';
import { AuthResponse } from '@casino/shared-types';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('register')
  @HttpCode(HttpStatus.CREATED)
  public async register(@Body() dto: RegisterRequestDto): Promise<AuthResponse> {
    return this.authService.register(dto.email, dto.username, dto.password);
  }

  @Post('login')
  @HttpCode(HttpStatus.OK)
  public async login(@Body() dto: LoginRequestDto): Promise<AuthResponse> {
    return this.authService.login(dto.emailOrUsername, dto.password);
  }

  @Post('refresh')
  @HttpCode(HttpStatus.OK)
  public async refresh(
    @Body() dto: RefreshTokenRequestDto,
  ): Promise<{ accessToken: string; refreshToken: string; expiresIn: number }> {
    return this.authService.refreshToken(dto.refreshToken);
  }

  @Post('logout')
  @HttpCode(HttpStatus.OK)
  public async logout(@Body() dto: RefreshTokenRequestDto): Promise<{ success: boolean }> {
    return this.authService.logout(dto.refreshToken);
  }

  @Post('forgot-password')
  @HttpCode(HttpStatus.OK)
  public async forgotPassword(
    @Body('email') email: string,
  ): Promise<{ success: boolean; message: string }> {
    return this.authService.forgotPassword(email);
  }

  @Post('reset-password')
  @HttpCode(HttpStatus.OK)
  public async resetPassword(
    @Body('token') token: string,
    @Body('newPassword') newPassword: string,
  ): Promise<{ success: boolean }> {
    return this.authService.resetPassword(token, newPassword);
  }
}
