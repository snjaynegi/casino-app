import { Injectable } from '@nestjs/common';
import * as crypto from 'crypto';

export interface StoredUser {
  id: string;
  email: string;
  username: string;
  passwordHash: string;
  role: 'PLAYER' | 'ADMIN_SUPER' | 'ADMIN_OPS' | 'ADMIN_SUPPORT';
  status: 'ACTIVE' | 'SUSPENDED' | 'DELETED';
  createdAt: Date;
  updatedAt: Date;
}

export interface StoredProfile {
  userId: string;
  displayName: string;
  avatarUrl?: string;
  level: number;
  experiencePoints: number;
  dailyTimeLimitMinutes?: number;
  coolOffUntil?: Date;
  createdAt: Date;
  updatedAt: Date;
}

export interface StoredSession {
  id: string;
  userId: string;
  refreshTokenHash: string;
  isRevoked: boolean;
  expiresAt: Date;
  createdAt: Date;
}

export interface StoredWallet {
  id: string;
  userId: string;
  balance: number;
  currencyCode: 'VCOIN';
  updatedAt: Date;
}

@Injectable()
export class UserStoreService {
  private readonly users = new Map<string, StoredUser>();
  private readonly profiles = new Map<string, StoredProfile>();
  private readonly sessions = new Map<string, StoredSession>();
  private readonly wallets = new Map<string, StoredWallet>();
  private readonly passwordResetTokens = new Map<string, { userId: string; expiresAt: Date }>();

  public async createUser(
    email: string,
    username: string,
    passwordHash: string,
  ): Promise<StoredUser> {
    const id = crypto.randomUUID();
    const now = new Date();

    const user: StoredUser = {
      id,
      email: email.toLowerCase().trim(),
      username: username.trim(),
      passwordHash,
      role: 'PLAYER',
      status: 'ACTIVE',
      createdAt: now,
      updatedAt: now,
    };

    const profile: StoredProfile = {
      userId: id,
      displayName: username,
      level: 1,
      experiencePoints: 0,
      createdAt: now,
      updatedAt: now,
    };

    const wallet: StoredWallet = {
      id: crypto.randomUUID(),
      userId: id,
      balance: 1000, // Initial grant
      currencyCode: 'VCOIN',
      updatedAt: now,
    };

    this.users.set(id, user);
    this.profiles.set(id, profile);
    this.wallets.set(id, wallet);

    return user;
  }

  public async findByEmail(email: string): Promise<StoredUser | null> {
    const normalized = email.toLowerCase().trim();
    for (const u of this.users.values()) {
      if (u.email === normalized && u.status !== 'DELETED') return u;
    }
    return null;
  }

  public async findByUsername(username: string): Promise<StoredUser | null> {
    const normalized = username.toLowerCase().trim();
    for (const u of this.users.values()) {
      if (u.username.toLowerCase() === normalized && u.status !== 'DELETED') return u;
    }
    return null;
  }

  public async findById(id: string): Promise<StoredUser | null> {
    const user = this.users.get(id);
    if (!user || user.status === 'DELETED') return null;
    return user;
  }

  public async getProfile(userId: string): Promise<StoredProfile | null> {
    return this.profiles.get(userId) || null;
  }

  public async updateProfile(
    userId: string,
    partial: Partial<StoredProfile>,
  ): Promise<StoredProfile> {
    const existing = this.profiles.get(userId);
    if (!existing) throw new Error('Profile not found');

    const updated = {
      ...existing,
      ...partial,
      updatedAt: new Date(),
    };
    this.profiles.set(userId, updated);
    return updated;
  }

  public async getWallet(userId: string): Promise<StoredWallet | null> {
    return this.wallets.get(userId) || null;
  }

  public async updateWalletBalance(userId: string, newBalance: number): Promise<StoredWallet> {
    const existing = this.wallets.get(userId);
    if (!existing) throw new Error('Wallet not found');

    existing.balance = newBalance;
    existing.updatedAt = new Date();
    return existing;
  }

  public async createSession(userId: string, refreshToken: string): Promise<StoredSession> {
    const id = crypto.randomUUID();
    const refreshTokenHash = crypto.createHash('sha256').update(refreshToken).digest('hex');

    const session: StoredSession = {
      id,
      userId,
      refreshTokenHash,
      isRevoked: false,
      expiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000),
      createdAt: new Date(),
    };

    this.sessions.set(session.id, session);
    return session;
  }

  public async findSessionByToken(refreshToken: string): Promise<StoredSession | null> {
    const hash = crypto.createHash('sha256').update(refreshToken).digest('hex');
    for (const s of this.sessions.values()) {
      if (s.refreshTokenHash === hash) return s;
    }
    return null;
  }

  public async revokeSession(sessionId: string): Promise<void> {
    const session = this.sessions.get(sessionId);
    if (session) {
      session.isRevoked = true;
    }
  }

  public async revokeAllUserSessions(userId: string): Promise<void> {
    for (const s of this.sessions.values()) {
      if (s.userId === userId) {
        s.isRevoked = true;
      }
    }
  }

  public async createPasswordResetToken(userId: string): Promise<string> {
    const token = crypto.randomBytes(24).toString('hex');
    this.passwordResetTokens.set(token, {
      userId,
      expiresAt: new Date(Date.now() + 3600 * 1000), // 1 hour
    });
    return token;
  }

  public async verifyAndConsumeResetToken(token: string): Promise<string | null> {
    const entry = this.passwordResetTokens.get(token);
    if (!entry) return null;
    if (entry.expiresAt < new Date()) {
      this.passwordResetTokens.delete(token);
      return null;
    }
    this.passwordResetTokens.delete(token);
    return entry.userId;
  }

  public async updatePassword(userId: string, newPasswordHash: string): Promise<void> {
    const user = this.users.get(userId);
    if (user) {
      user.passwordHash = newPasswordHash;
      user.updatedAt = new Date();
    }
  }

  public async softDeleteUser(userId: string): Promise<void> {
    const user = this.users.get(userId);
    if (user) {
      user.status = 'DELETED';
      user.updatedAt = new Date();
      await this.revokeAllUserSessions(userId);
    }
  }
}
