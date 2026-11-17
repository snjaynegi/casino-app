import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { TokenService, JwtPayload } from '../auth/jwt.service';

@Injectable()
export class AdminRoleGuard implements CanActivate {
  constructor(private readonly tokenService: TokenService) {}

  public canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest();
    const authHeader = request.headers['authorization'];

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new UnauthorizedException('Missing admin authorization token');
    }

    const token = authHeader.substring(7);
    const payload = this.tokenService.verifyToken<JwtPayload>(token);

    if (!payload.role || !payload.role.startsWith('ADMIN_')) {
      throw new ForbiddenException('Forbidden: Requires administrative credentials');
    }

    request.user = payload;
    return true;
  }
}
