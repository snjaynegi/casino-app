import {
  BadRequestException,
  ConflictException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import * as bcrypt from 'bcrypt';
import { UserStoreService } from './user-store.service';
import { TokenService } from './jwt.service';
import { AuthResponse } from '@casino/shared-types';

@Injectable()
export class AuthService {
  private readonly bcryptSaltRounds = 12;

  constructor(
    private readonly userStore: UserStoreService,
    private readonly tokenService: TokenService,
  ) {}

  public async register(email: string, username: string, password: string): Promise<AuthResponse> {
    if (!email || !email.includes('@')) {
      throw new BadRequestException('A valid email address is required.');
    }
    if (!username || username.trim().length < 3) {
      throw new BadRequestException('Username must be at least 3 characters long.');
    }
    if (!password || password.length < 8) {
      throw new BadRequestException('Password must be at least 8 characters long.');
    }

    const existingEmail = await this.userStore.findByEmail(email);
    if (existingEmail) {
      throw new ConflictException('An account with this email already exists.');
    }

    const existingUsername = await this.userStore.findByUsername(username);
    if (existingUsername) {
      throw new ConflictException('This username is already taken.');
    }

    const passwordHash = await bcrypt.hash(password, this.bcryptSaltRounds);
    const user = await this.userStore.createUser(email, username, passwordHash);

    const accessToken = this.tokenService.signAccessToken({
      sub: user.id,
      email: user.email,
      username: user.username,
      role: user.role,
    });

    const refreshToken = this.tokenService.signRefreshToken(user.id);
    await this.userStore.createSession(user.id, refreshToken);

    return {
      user: {
        id: user.id,
        email: user.email,
        username: user.username,
        role: user.role,
        createdAt: user.createdAt.toISOString(),
      },
      tokens: {
        accessToken,
        refreshToken,
        expiresIn: 900,
      },
    };
  }

  public async login(identifier: string, password: string): Promise<AuthResponse> {
    if (!identifier || !password) {
      throw new BadRequestException('Identifier and password are required.');
    }

    const user = identifier.includes('@')
      ? await this.userStore.findByEmail(identifier)
      : await this.userStore.findByUsername(identifier);

    if (!user) {
      throw new UnauthorizedException('Invalid credentials.');
    }

    if (user.status === 'SUSPENDED') {
      throw new UnauthorizedException('Account has been suspended for policy violations.');
    }

    const isMatch = await bcrypt.compare(password, user.passwordHash);
    if (!isMatch) {
      throw new UnauthorizedException('Invalid credentials.');
    }

    const accessToken = this.tokenService.signAccessToken({
      sub: user.id,
      email: user.email,
      username: user.username,
      role: user.role,
    });

    const refreshToken = this.tokenService.signRefreshToken(user.id);
    await this.userStore.createSession(user.id, refreshToken);

    return {
      user: {
        id: user.id,
        email: user.email,
        username: user.username,
        role: user.role,
        createdAt: user.createdAt.toISOString(),
      },
      tokens: {
        accessToken,
        refreshToken,
        expiresIn: 900,
      },
    };
  }

  public async refreshToken(
    oldRefreshToken: string,
  ): Promise<{ accessToken: string; refreshToken: string; expiresIn: number }> {
    if (!oldRefreshToken) {
      throw new BadRequestException('Refresh token is required.');
    }

    this.tokenService.verifyToken(oldRefreshToken);
    const session = await this.userStore.findSessionByToken(oldRefreshToken);

    if (!session) {
      throw new UnauthorizedException('Invalid refresh session.');
    }

    if (session.isRevoked) {
      // Possible token reuse attack: revoke all user sessions
      await this.userStore.revokeAllUserSessions(session.userId);
      throw new UnauthorizedException('Refresh token reuse detected. All sessions revoked.');
    }

    const user = await this.userStore.findById(session.userId);
    if (!user || user.status !== 'ACTIVE') {
      throw new UnauthorizedException('User account inactive or deleted.');
    }

    // Revoke old session (Rotation)
    await this.userStore.revokeSession(session.id);

    // Issue new pair
    const accessToken = this.tokenService.signAccessToken({
      sub: user.id,
      email: user.email,
      username: user.username,
      role: user.role,
    });

    const newRefreshToken = this.tokenService.signRefreshToken(user.id);
    await this.userStore.createSession(user.id, newRefreshToken);

    return {
      accessToken,
      refreshToken: newRefreshToken,
      expiresIn: 900,
    };
  }

  public async logout(refreshToken: string): Promise<{ success: boolean }> {
    if (refreshToken) {
      const session = await this.userStore.findSessionByToken(refreshToken);
      if (session) {
        await this.userStore.revokeSession(session.id);
      }
    }
    return { success: true };
  }

  public async forgotPassword(
    email: string,
  ): Promise<{ success: boolean; message: string; resetToken?: string }> {
    const user = await this.userStore.findByEmail(email);
    if (!user) {
      // Return success to avoid email enumeration
      return {
        success: true,
        message: 'If an account exists, a reset instructions token has been generated.',
      };
    }

    const token = await this.userStore.createPasswordResetToken(user.id);
    return {
      success: true,
      message: 'Reset instructions token generated successfully.',
      resetToken: process.env.NODE_ENV !== 'production' ? token : undefined,
    };
  }

  public async resetPassword(token: string, newPassword: string): Promise<{ success: boolean }> {
    if (!newPassword || newPassword.length < 8) {
      throw new BadRequestException('Password must be at least 8 characters long.');
    }

    const userId = await this.userStore.verifyAndConsumeResetToken(token);
    if (!userId) {
      throw new BadRequestException('Invalid or expired password reset token.');
    }

    const newHash = await bcrypt.hash(newPassword, this.bcryptSaltRounds);
    await this.userStore.updatePassword(userId, newHash);
    await this.userStore.revokeAllUserSessions(userId);

    return { success: true };
  }
}
