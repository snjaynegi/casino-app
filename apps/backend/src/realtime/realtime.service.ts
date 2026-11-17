import { Injectable } from '@nestjs/common';
import { RocketRescueEngine } from '../games/rocket-rescue/rocket-rescue.engine';

export interface LiveRoundState {
  roundId: string;
  phase: 'COUNTDOWN' | 'FLYING' | 'CRASHED';
  countdownSeconds: number;
  multiplier: number;
  elapsedSeconds: number;
  serverSeedHash: string;
}

export interface LiveBetEntry {
  username: string;
  betAmount: number;
  multiplier?: number;
  payout?: number;
  status: 'BETTING' | 'CASHED_OUT' | 'CRASHED';
}

@Injectable()
export class RealtimeService {
  private currentLiveRound: LiveRoundState = {
    roundId: 'LIVE-ROUND-100',
    phase: 'COUNTDOWN',
    countdownSeconds: 5,
    multiplier: 1.0,
    elapsedSeconds: 0,
    serverSeedHash: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
  };

  private readonly liveBets = new Map<string, LiveBetEntry>();
  private connectedUserCount = 0;

  public incrementPresence() {
    this.connectedUserCount++;
  }

  public decrementPresence() {
    this.connectedUserCount = Math.max(0, this.connectedUserCount - 1);
  }

  public getOnlineCount(): number {
    return this.connectedUserCount;
  }

  public getLiveRoundState(): LiveRoundState {
    return this.currentLiveRound;
  }

  public registerLiveBet(username: string, betAmount: number): LiveBetEntry {
    const entry: LiveBetEntry = {
      username,
      betAmount,
      status: 'BETTING',
    };
    this.liveBets.set(username, entry);
    return entry;
  }

  public registerLiveCashout(
    username: string,
    multiplier: number,
    payout: number,
  ): LiveBetEntry | null {
    const entry = this.liveBets.get(username);
    if (entry) {
      entry.status = 'CASHED_OUT';
      entry.multiplier = multiplier;
      entry.payout = payout;
      return entry;
    }
    return null;
  }

  public getActiveBets(): LiveBetEntry[] {
    return Array.from(this.liveBets.values());
  }

  public updateFlightTick(elapsedSeconds: number, crashMultiplier: number): LiveRoundState {
    const calculated = RocketRescueEngine.calculateMultiplierAtTime(elapsedSeconds);

    if (calculated >= crashMultiplier) {
      this.currentLiveRound = {
        ...this.currentLiveRound,
        phase: 'CRASHED',
        multiplier: crashMultiplier,
        elapsedSeconds,
      };
    } else {
      this.currentLiveRound = {
        ...this.currentLiveRound,
        phase: 'FLYING',
        multiplier: calculated,
        elapsedSeconds,
      };
    }

    return this.currentLiveRound;
  }
}
