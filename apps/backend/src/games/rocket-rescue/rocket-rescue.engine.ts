import { CrashCalculator, CrashOutcome } from '@casino/game-engine';

export interface RocketFlightTick {
  elapsedSeconds: number;
  currentMultiplier: number;
  isCrashed: boolean;
  hasCrystalBonus: boolean;
}

export class RocketRescueEngine {
  /**
   * Continuous curve formula matching audited crash mechanics:
   * Multiplier(t) = 1.00 + 0.06 * (t ^ 1.45)
   */
  public static calculateMultiplierAtTime(elapsedSeconds: number): number {
    if (elapsedSeconds <= 0) return 1.0;
    const raw = 1.0 + 0.06 * Math.pow(elapsedSeconds, 1.45);
    return Math.floor(raw * 100) / 100;
  }

  /**
   * Generates authoritative crash point from CSPRNG
   */
  public static generateCrashOutcome(
    serverSeed: string,
    clientSeed: string,
    nonce: number,
  ): CrashOutcome {
    return CrashCalculator.calculateCrashPoint(serverSeed, clientSeed, nonce);
  }

  /**
   * Inverts curve to find the exact flight duration (seconds) before crash
   */
  public static calculateCrashDuration(targetCrashMultiplier: number): number {
    if (targetCrashMultiplier <= 1.0) return 0.0;
    // Multiplier - 1.0 = 0.06 * (t ^ 1.45)
    // t ^ 1.45 = (Multiplier - 1.0) / 0.06
    // t = ((Multiplier - 1.0) / 0.06) ^ (1 / 1.45)
    const t = Math.pow((targetCrashMultiplier - 1.0) / 0.06, 1 / 1.45);
    return Math.round(t * 100) / 100;
  }
}
