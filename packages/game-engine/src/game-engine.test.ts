import { describe, it } from 'node:test';
import * as assert from 'node:assert';
import { ProvableFairnessEngine } from './provable-fairness';
import { CrashCalculator } from './crash-calculator';

describe('ProvableFairnessEngine', () => {
  it('should generate a 64-char hex server seed and valid SHA-256 hash', () => {
    const seed = ProvableFairnessEngine.generateServerSeed();
    assert.strictEqual(seed.length, 64);

    const hash = ProvableFairnessEngine.hashServerSeed(seed);
    assert.strictEqual(hash.length, 64);
    assert.strictEqual(ProvableFairnessEngine.verifyCommitment(seed, hash), true);
  });

  it('should deterministically produce identical outcomes given identical seeds and nonce', () => {
    const serverSeed = 'b1b9a2d8e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1';
    const clientSeed = 'client-test-seed-1';
    const nonce = 10;

    const res1 = ProvableFairnessEngine.computeOutcome(serverSeed, clientSeed, nonce);
    const res2 = ProvableFairnessEngine.computeOutcome(serverSeed, clientSeed, nonce);

    assert.strictEqual(res1.hash, res2.hash);
    assert.strictEqual(res1.normalizedFloat, res2.normalizedFloat);
    assert.ok(res1.normalizedFloat >= 0 && res1.normalizedFloat < 1);
  });
});

describe('CrashCalculator', () => {
  it('should calculate valid crash multipliers >= 1.00x', () => {
    const serverSeed = '1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef';
    const clientSeed = 'test-client';

    for (let nonce = 1; nonce <= 50; nonce++) {
      const outcome = CrashCalculator.calculateCrashPoint(serverSeed, clientSeed, nonce);
      assert.ok(
        outcome.crashMultiplier >= 1.0,
        `Multiplier must be >= 1.00, got ${outcome.crashMultiplier}`,
      );
      assert.strictEqual(outcome.nonce, nonce);
    }
  });
});
