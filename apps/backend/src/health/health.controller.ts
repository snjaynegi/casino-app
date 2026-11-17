import { Controller, Get } from '@nestjs/common';
import { HealthService } from './health.service';
import { HealthCheckResponseDto } from '@casino/api-contracts';

@Controller('health')
export class HealthController {
  constructor(private readonly healthService: HealthService) {}

  @Get()
  public check(): HealthCheckResponseDto {
    return this.healthService.getHealth();
  }
}
