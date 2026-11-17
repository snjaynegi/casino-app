import { RngService } from '../../rng/rng.service';

export interface DiceRollResult {
  target: number; // e.g. 50.00
  condition: 'OVER' | 'UNDER';
  rolledNumber: number; // 0.00 to 99.99
  winChance: number;
  multiplier: number;
  isWin: boolean;
}

export class DiceEngine {
  /**
   * Evaluates standard provably fair dice roll with 1.5% house edge
   */
  public static roll(
    rngService: RngService,
    serverSeed: string,
    clientSeed: string,
    nonce: number,
    target: number,
    condition: 'OVER' | 'UNDER',
  ): DiceRollResult {
    const derived = rngService.deriveOutcome(serverSeed, clientSeed, nonce);
    // Float [0, 1) to dice roll [0.00, 99.99]
    const rolledNumber = Math.floor(derived.normalizedFloat * 10000) / 100;

    const winChance = condition === 'OVER' ? 100 - target : target;
    const houseEdgeMultiplier = 98.5; // 1.5% house edge
    const multiplier = Math.floor((houseEdgeMultiplier / winChance) * 100) / 100;

    const isWin = condition === 'OVER' ? rolledNumber > target : rolledNumber < target;

    return {
      target,
      condition,
      rolledNumber,
      winChance,
      multiplier,
      isWin,
    };
  }
}
