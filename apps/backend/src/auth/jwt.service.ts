import { Injectable, UnauthorizedException } from '@nestjs/common';
import * as jwt from 'jsonwebtoken';
import * as crypto from 'crypto';

export interface JwtPayload {
  sub: string; // userId
  email: string;
  username: string;
  role: string;
}

@Injectable()
export class TokenService {
  private readonly jwtSecret =
    process.env.JWT_SECRET || 'dev_super_secret_jwt_key_256_bits_minimum_length_required';
  private readonly accessExpiresIn = '15m';
  private readonly refreshExpiresIn = '7d';

  public signAccessToken(payload: JwtPayload): string {
    return jwt.sign(payload, this.jwtSecret, { expiresIn: this.accessExpiresIn });
  }

  public signRefreshToken(userId: string): string {
    return jwt.sign({ sub: userId, type: 'refresh', jti: crypto.randomUUID() }, this.jwtSecret, {
      expiresIn: this.refreshExpiresIn,
    });
  }

  public verifyToken<T extends object = JwtPayload>(token: string): T {
    try {
      return jwt.verify(token, this.jwtSecret) as T;
    } catch {
      throw new UnauthorizedException('Invalid or expired authentication token');
    }
  }
}
