// User & Auth Types
export type UserRole =
  'PLAYER' | 'ADMIN_SUPER' | 'ADMIN_OPS' | 'ADMIN_SUPPORT' | 'ADMIN_GAME_MGR' | 'ADMIN_ANALYST';

export interface UserSummary {
  id: string;
  email: string;
  username: string;
  role: UserRole;
  createdAt: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface AuthResponse {
  user: UserSummary;
  tokens: AuthTokens;
}

// Wallet & Ledger Types
export type TransactionType =
  'INITIAL_GRANT' | 'DAILY_REWARD' | 'BET_DEBIT' | 'WIN_CREDIT' | 'REFUND' | 'ACHIEVEMENT';

export interface WalletState {
  userId: string;
  balance: number;
  currencyCode: 'VCOIN';
  updatedAt: string;
}

export interface LedgerTransaction {
  id: string;
  walletId: string;
  userId: string;
  transactionType: TransactionType;
  amount: number;
  previousBalance: number;
  newBalance: number;
  referenceId?: string;
  idempotencyKey: string;
  createdAt: string;
}

// Game Types
export type GameCategory = 'CRASH' | 'ARCADE' | 'INSTANT';

export type GameSessionStatus = 'INITIALIZED' | 'ACTIVE' | 'RESOLVED' | 'CANCELLED';

export interface GameMetadata {
  id: string;
  title: string;
  category: GameCategory;
  description: string;
  minBet: number;
  maxBet: number;
  isActive: boolean;
  thumbnailUrl: string;
}

// Provable Fairness Types
export interface FairnessCommitment {
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
}

export interface FairnessVerificationRecord {
  sessionId: string;
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  derivedOutcome: number;
  verificationHash: string;
  isVerified: boolean;
}

// Standard API Response Envelope
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp: string;
}
