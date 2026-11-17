import * as crypto from 'crypto';

export interface ProvableFairnessResult {
  serverSeed: string;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
  hash: string;
  normalizedFloat: number; // 0.0000000 to 0.9999999
}

export class ProvableFairnessEngine {
  /**
   * Generates a 256-bit cryptographically secure server seed
   */
  public static generateServerSeed(): string {
    return crypto.randomBytes(32).toString('hex');
  }

  /**
   * Hashes a server seed using SHA-256 for public commitment before a round
   */
  public static hashServerSeed(serverSeed: string): string {
    return crypto.createHash('sha256').update(serverSeed).digest('hex');
  }

  /**
   * Computes the HMAC-SHA512 of (serverSeed, clientSeed + ":" + nonce)
   * and extracts a normalized deterministic float between [0, 1)
   */
  public static computeOutcome(
    serverSeed: string,
    clientSeed: string,
    nonce: number,
  ): ProvableFairnessResult {
    const message = `${clientSeed}:${nonce}`;
    const hmac = crypto.createHmac('sha512', serverSeed);
    hmac.update(message);
    const hash = hmac.digest('hex');

    // Convert the first 52 bits (13 hex characters) of the hash to a float [0, 1)
    // 52 bits provides 15-17 decimal digits of precision matching IEEE 754 float
    const subHash = hash.substring(0, 13);
    const intVal = parseInt(subHash, 16);
    const maxVal = Math.pow(2, 52);
    const normalizedFloat = intVal / maxVal;

    return {
      serverSeed,
      serverSeedHash: this.hashServerSeed(serverSeed),
      clientSeed,
      nonce,
      hash,
      normalizedFloat,
    };
  }

  /**
   * Verifies that a given outcome matches the revealed server seed and client parameters
   */
  public static verifyCommitment(serverSeed: string, expectedHash: string): boolean {
    return this.hashServerSeed(serverSeed) === expectedHash;
  }
}
