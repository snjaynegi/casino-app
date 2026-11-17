import { describe, it } from 'node:test';
import * as assert from 'node:assert';
import { HealthService } from './health.service';

describe('HealthService', () => {
  it('should return valid health status response', () => {
    const service = new HealthService();
    const result = service.getHealth();

    assert.strictEqual(result.status, 'ok');
    assert.strictEqual(typeof result.timestamp, 'string');
    assert.strictEqual(typeof result.uptime, 'number');
    assert.strictEqual(result.database, 'connected');
    assert.strictEqual(result.redis, 'connected');
  });
});
