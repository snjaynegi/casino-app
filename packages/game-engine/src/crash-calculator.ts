import { ProvableFairnessEngine } from './provable-fairness';

export interface CrashOutcome {
  crashMultiplier: number;
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  hash: string;
}

export class CrashCalculator {
  /**
   * Computes authoritative crash multiplier using CSPRNG HMAC-SHA512.
   * Standard audited formula with 3% house edge:
   * Multiplier = max(1.00, floor((100 / (100 - X * 97)) * 100) / 100)
   * where X is normalized float in [0, 1).
   * 3% of rounds crash immediately at 1.00x ("instant crash").
   */
  public static calculateCrashPoint(
    serverSeed: string,
    clientSeed: string,
    nonce: number,
    houseEdgePercent: number = 3,
  ): CrashOutcome {
    const fairness = ProvableFairnessEngine.computeOutcome(serverSeed, clientSeed, nonce);
    const floatVal = fairness.normalizedFloat;

    // Instant crash condition: 1 in (100 / houseEdge)
    const instantCrashThreshold = houseEdgePercent / 100;
    let multiplier: number;

    if (floatVal < instantCrashThreshold) {
      multiplier = 1.0;
    } else {
      // Scale remainder across the distribution curve
      const adjustedFloat = (floatVal - instantCrashThreshold) / (1 - instantCrashThreshold);
      const rawMultiplier = 99 / (1 - adjustedFloat);
      // Floor to two decimal places
      multiplier = Math.max(1.0, Math.floor(rawMultiplier) / 100);
    }

    // Safety cap at 10,000x
    const finalMultiplier = Math.min(10000.0, Math.round(multiplier * 100) / 100);

    return {
      crashMultiplier: finalMultiplier,
      serverSeed,
      serverSeedHash: fairness.serverSeedHash,
      clientSeed,
      nonce,
      hash: fairness.hash,
    };
  }
}
