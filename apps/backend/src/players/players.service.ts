import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { UserStoreService } from '../auth/user-store.service';

export interface PlayerMeDto {
  id: string;
  email: string;
  username: string;
  displayName: string;
  avatarUrl?: string;
  level: number;
  experiencePoints: number;
  balance: number;
  currencyCode: 'VCOIN';
  dailyTimeLimitMinutes?: number;
  coolOffUntil?: string;
  createdAt: string;
}

@Injectable()
export class PlayersService {
  constructor(private readonly userStore: UserStoreService) {}

  public async getMe(userId: string): Promise<PlayerMeDto> {
    const user = await this.userStore.findById(userId);
    if (!user) throw new NotFoundException('Player not found');

    const profile = await this.userStore.getProfile(userId);
    const wallet = await this.userStore.getWallet(userId);

    return {
      id: user.id,
      email: user.email,
      username: user.username,
      displayName: profile?.displayName || user.username,
      avatarUrl: profile?.avatarUrl,
      level: profile?.level || 1,
      experiencePoints: profile?.experiencePoints || 0,
      balance: wallet?.balance || 0,
      currencyCode: 'VCOIN',
      dailyTimeLimitMinutes: profile?.dailyTimeLimitMinutes,
      coolOffUntil: profile?.coolOffUntil ? profile?.coolOffUntil.toISOString() : undefined,
      createdAt: user.createdAt.toISOString(),
    };
  }

  public async updateMe(
    userId: string,
    updates: {
      displayName?: string;
      avatarUrl?: string;
      dailyTimeLimitMinutes?: number;
      coolOffDays?: number;
    },
  ): Promise<PlayerMeDto> {
    const patch: any = {};
    if (updates.displayName !== undefined) patch.displayName = updates.displayName.trim();
    if (updates.avatarUrl !== undefined) patch.avatarUrl = updates.avatarUrl;
    if (updates.dailyTimeLimitMinutes !== undefined) {
      if (updates.dailyTimeLimitMinutes < 0)
        throw new BadRequestException('Time limit cannot be negative');
      patch.dailyTimeLimitMinutes = updates.dailyTimeLimitMinutes;
    }
    if (updates.coolOffDays !== undefined && updates.coolOffDays > 0) {
      patch.coolOffUntil = new Date(Date.now() + updates.coolOffDays * 24 * 3600 * 1000);
    }

    await this.userStore.updateProfile(userId, patch);
    return this.getMe(userId);
  }

  public async deleteMe(userId: string): Promise<{ success: boolean; message: string }> {
    await this.userStore.softDeleteUser(userId);
    return {
      success: true,
      message:
        'Account and active sessions have been permanently deleted in accordance with data privacy regulations.',
    };
  }
}
