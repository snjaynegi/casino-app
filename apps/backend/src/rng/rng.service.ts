import { Injectable } from '@nestjs/common';
import * as crypto from 'crypto';

export interface GeneratedCommitment {
  serverSeed: string;
  serverSeedHash: string;
}

export interface DerivedFairnessOutcome {
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  hash: string;
  normalizedFloat: number; // [0, 1)
}

@Injectable()
export class RngService {
  /**
   * Generates a 256-bit cryptographically secure server seed using OS entropy
   */
  public generateServerSeed(): GeneratedCommitment {
    const serverSeed = crypto.randomBytes(32).toString('hex');
    const serverSeedHash = crypto.createHash('sha256').update(serverSeed).digest('hex');
    return { serverSeed, serverSeedHash };
  }

  /**
   * Derives a deterministic outcome from Server Seed, Client Seed, and Nonce using HMAC-SHA512
   */
  public deriveOutcome(
    serverSeed: string,
    clientSeed: string,
    nonce: number,
  ): DerivedFairnessOutcome {
    const message = `${clientSeed}:${nonce}`;
    const hmac = crypto.createHmac('sha512', serverSeed);
    hmac.update(message);
    const hash = hmac.digest('hex');

    // Extract first 52 bits for IEEE 754 float precision
    const subHash = hash.substring(0, 13);
    const intVal = parseInt(subHash, 16);
    const maxVal = Math.pow(2, 52);
    const normalizedFloat = intVal / maxVal;

    const serverSeedHash = crypto.createHash('sha256').update(serverSeed).digest('hex');

    return {
      serverSeed,
      serverSeedHash,
      clientSeed,
      nonce,
      hash,
      normalizedFloat,
    };
  }

  /**
   * Public verification helper: re-computes outcome from revealed parameters
   */
  public verifyOutcome(
    serverSeed: string,
    clientSeed: string,
    nonce: number,
    expectedHash: string,
  ): { isValid: boolean; recomputedHash: string; normalizedFloat: number } {
    const derived = this.deriveOutcome(serverSeed, clientSeed, nonce);
    const isValid = derived.hash === expectedHash;
    return {
      isValid,
      recomputedHash: derived.hash,
      normalizedFloat: derived.normalizedFloat,
    };
  }

  /**
   * Chi-Square Goodness-of-Fit statistical distribution test
   * Validates that outcomes are uniformly distributed across bins with no bias.
   */
  public runChiSquareUniformityTest(
    sampleSize: number = 10000,
    binsCount: number = 10,
  ): {
    chiSquare: number;
    degreesOfFreedom: number;
    passed: boolean;
  } {
    const counts = new Array(binsCount).fill(0);
    const expected = sampleSize / binsCount;

    const serverSeed = this.generateServerSeed().serverSeed;
    const clientSeed = 'statistical-validation-client-seed';

    for (let nonce = 1; nonce <= sampleSize; nonce++) {
      const outcome = this.deriveOutcome(serverSeed, clientSeed, nonce);
      const binIndex = Math.min(binsCount - 1, Math.floor(outcome.normalizedFloat * binsCount));
      counts[binIndex]++;
    }

    let chiSquare = 0;
    for (let i = 0; i < binsCount; i++) {
      const diff = counts[i] - expected;
      chiSquare += (diff * diff) / expected;
    }

    const degreesOfFreedom = binsCount - 1;
    // Critical value for df = 9 at alpha = 0.01 is 21.666
    const criticalValueAlpha01 = 21.666;
    const passed = chiSquare < criticalValueAlpha01;

    return { chiSquare, degreesOfFreedom, passed };
  }
}
