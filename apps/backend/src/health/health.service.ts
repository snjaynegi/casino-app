import { Injectable } from '@nestjs/common';
import { HealthCheckResponseDto } from '@casino/api-contracts';

@Injectable()
export class HealthService {
  private readonly startTime = Date.now();

  public getHealth(): HealthCheckResponseDto {
    const uptimeSeconds = Math.floor((Date.now() - this.startTime) / 1000);

    return {
      status: 'ok',
      timestamp: new Date().toISOString(),
      uptime: uptimeSeconds,
      database: 'connected',
      redis: 'connected',
      environment: process.env.NODE_ENV || 'development',
    };
  }
}
