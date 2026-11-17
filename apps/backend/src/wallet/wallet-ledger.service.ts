import { BadRequestException, Injectable } from '@nestjs/common';
import { UserStoreService } from '../auth/user-store.service';
import * as crypto from 'crypto';

export interface StoredLedgerTransaction {
  id: string;
  walletId: string;
  userId: string;
  transactionType:
    'INITIAL_GRANT' | 'DAILY_REWARD' | 'BET_DEBIT' | 'WIN_CREDIT' | 'REFUND' | 'ACHIEVEMENT';
  amount: number;
  previousBalance: number;
  newBalance: number;
  referenceId?: string;
  idempotencyKey: string;
  createdAt: Date;
}

@Injectable()
export class WalletLedgerService {
  private readonly transactions: StoredLedgerTransaction[] = [];
  private readonly idempotencyMap = new Map<string, StoredLedgerTransaction>();
  private readonly inFlightOperations = new Map<string, Promise<StoredLedgerTransaction>>();

  constructor(private readonly userStore: UserStoreService) {}

  public async getBalance(userId: string): Promise<number> {
    const wallet = await this.userStore.getWallet(userId);
    return wallet?.balance ?? 0;
  }

  public async debit(
    userId: string,
    amount: number,
    transactionType: StoredLedgerTransaction['transactionType'],
    idempotencyKey: string,
    referenceId?: string,
  ): Promise<StoredLedgerTransaction> {
    if (amount <= 0) {
      throw new BadRequestException('Debit amount must be positive.');
    }

    // Idempotency check: if key already processed or currently in-flight, return same promise/result
    if (this.idempotencyMap.has(idempotencyKey)) {
      return this.idempotencyMap.get(idempotencyKey)!;
    }
    if (this.inFlightOperations.has(idempotencyKey)) {
      return this.inFlightOperations.get(idempotencyKey)!;
    }

    const opPromise = (async () => {
      try {
        const wallet = await this.userStore.getWallet(userId);
        if (!wallet) throw new BadRequestException('Wallet not found');

        if (wallet.balance < amount) {
          throw new BadRequestException('Insufficient virtual coin balance.');
        }

        const previousBalance = wallet.balance;
        const newBalance = previousBalance - amount;

        await this.userStore.updateWalletBalance(userId, newBalance);

        const tx: StoredLedgerTransaction = {
          id: crypto.randomUUID(),
          walletId: wallet.id,
          userId,
          transactionType,
          amount,
          previousBalance,
          newBalance,
          referenceId,
          idempotencyKey,
          createdAt: new Date(),
        };

        this.transactions.push(tx);
        this.idempotencyMap.set(idempotencyKey, tx);
        return tx;
      } finally {
        this.inFlightOperations.delete(idempotencyKey);
      }
    })();

    this.inFlightOperations.set(idempotencyKey, opPromise);
    return opPromise;
  }

  public async credit(
    userId: string,
    amount: number,
    transactionType: StoredLedgerTransaction['transactionType'],
    idempotencyKey: string,
    referenceId?: string,
  ): Promise<StoredLedgerTransaction> {
    if (amount <= 0) {
      throw new BadRequestException('Credit amount must be positive.');
    }

    if (this.idempotencyMap.has(idempotencyKey)) {
      return this.idempotencyMap.get(idempotencyKey)!;
    }
    if (this.inFlightOperations.has(idempotencyKey)) {
      return this.inFlightOperations.get(idempotencyKey)!;
    }

    const opPromise = (async () => {
      try {
        const wallet = await this.userStore.getWallet(userId);
        if (!wallet) throw new BadRequestException('Wallet not found');

        const previousBalance = wallet.balance;
        const newBalance = previousBalance + amount;

        await this.userStore.updateWalletBalance(userId, newBalance);

        const tx: StoredLedgerTransaction = {
          id: crypto.randomUUID(),
          walletId: wallet.id,
          userId,
          transactionType,
          amount,
          previousBalance,
          newBalance,
          referenceId,
          idempotencyKey,
          createdAt: new Date(),
        };

        this.transactions.push(tx);
        this.idempotencyMap.set(idempotencyKey, tx);
        return tx;
      } finally {
        this.inFlightOperations.delete(idempotencyKey);
      }
    })();

    this.inFlightOperations.set(idempotencyKey, opPromise);
    return opPromise;
  }

  public getHistory(userId: string, limit: number = 20): StoredLedgerTransaction[] {
    return this.transactions
      .filter((t) => t.userId === userId)
      .sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
      .slice(0, limit);
  }
}
