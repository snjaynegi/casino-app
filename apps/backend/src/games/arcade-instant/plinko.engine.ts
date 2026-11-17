import { RngService } from '../../rng/rng.service';

export interface PlinkoDropResult {
  rows: number;
  risk: 'LOW' | 'MEDIUM' | 'HIGH';
  path: ('L' | 'R')[];
  slotIndex: number;
  multiplier: number;
}

export class PlinkoEngine {
  private static readonly PAYOUT_TABLES: Record<string, Record<number, number[]>> = {
    MEDIUM: {
      8: [13.0, 3.0, 1.3, 0.7, 0.4, 0.7, 1.3, 3.0, 13.0],
      10: [22.0, 5.0, 2.0, 1.4, 0.6, 0.4, 0.6, 1.4, 2.0, 5.0, 22.0],
      12: [33.0, 11.0, 4.0, 2.0, 1.1, 0.6, 0.3, 0.6, 1.1, 2.0, 4.0, 11.0, 33.0],
    },
  };

  /**
   * Generates peg bounce binary walk path driven by CSPRNG
   */
  public static simulateDrop(
    rngService: RngService,
    serverSeed: string,
    clientSeed: string,
    nonce: number,
    rows: number = 8,
    risk: 'LOW' | 'MEDIUM' | 'HIGH' = 'MEDIUM',
  ): PlinkoDropResult {
    const path: ('L' | 'R')[] = [];
    let slotIndex = 0;

    for (let r = 0; r < rows; r++) {
      const derived = rngService.deriveOutcome(serverSeed, clientSeed, nonce * 50 + r);
      const isRight = derived.normalizedFloat >= 0.5;
      path.push(isRight ? 'R' : 'L');
      if (isRight) slotIndex++;
    }

    const table = this.PAYOUT_TABLES[risk]?.[rows] || this.PAYOUT_TABLES['MEDIUM'][8];
    const multiplier = table[slotIndex] ?? 1.0;

    return {
      rows,
      risk,
      path,
      slotIndex,
      multiplier,
    };
  }
}
