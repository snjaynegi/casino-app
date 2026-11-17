export * from '@casino/shared-types';

export interface RegisterRequestDto {
  email: string;
  username: string;
  password: string;
}

export interface LoginRequestDto {
  emailOrUsername: string;
  password: string;
}

export interface RefreshTokenRequestDto {
  refreshToken: string;
}

export interface CreateGameSessionRequestDto {
  gameId: string;
  betAmount: number;
  clientSeed?: string;
}

export interface GameSessionResponseDto {
  sessionId: string;
  gameId: string;
  betAmount: number;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  status: string;
  createdAt: string;
}

export interface ClaimRewardRequestDto {
  rewardId: string;
}

export interface HealthCheckResponseDto {
  status: 'ok' | 'degraded' | 'error';
  timestamp: string;
  uptime: number;
  database: 'connected' | 'disconnected';
  redis: 'connected' | 'disconnected';
  environment: string;
}
