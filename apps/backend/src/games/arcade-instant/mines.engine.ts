import { RngService } from '../../rng/rng.service';

export interface MinesRoundResult {
  roundId: string;
  mineCount: number;
  minePositions: number[]; // 0 to 24
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
}

export class MinesEngine {
  /**
   * Generates 25-tile grid containing M mines using Fisher-Yates shuffle driven by CSPRNG
   */
  public static generateMinePositions(
    rngService: RngService,
    serverSeed: string,
    clientSeed: string,
    nonce: number,
    mineCount: number,
  ): number[] {
    const tiles = Array.from({ length: 25 }, (_, i) => i);

    for (let i = tiles.length - 1; i > 0; i--) {
      const derived = rngService.deriveOutcome(serverSeed, clientSeed, nonce * 100 + i);
      const j = Math.floor(derived.normalizedFloat * (i + 1));
      const temp = tiles[i];
      tiles[i] = tiles[j];
      tiles[j] = temp;
    }

    return tiles.slice(0, mineCount).sort((a, b) => a - b);
  }

  /**
   * Standard audited Mines multiplier formula with 2% house edge:
   * Multiplier(gemsRevealed) = 0.98 * (C(25, gemsRevealed) / C(25 - mineCount, gemsRevealed))
   */
  public static calculateMultiplier(mineCount: number, gemsRevealed: number): number {
    if (gemsRevealed <= 0) return 1.0;
    if (gemsRevealed > 25 - mineCount) return 0.0;

    let probability = 1.0;
    for (let i = 0; i < gemsRevealed; i++) {
      probability *= (25 - mineCount - i) / (25 - i);
    }

    const houseEdge = 0.98;
    const raw = houseEdge / probability;
    return Math.floor(raw * 100) / 100;
  }
}
