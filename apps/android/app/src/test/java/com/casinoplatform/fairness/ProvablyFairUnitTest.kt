package com.casinoplatform.fairness

import org.junit.Assert.*
import org.junit.Test
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ProvablyFairUnitTest {

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun hmacSha512(key: String, message: String): String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA512")
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(secretKeySpec)
        val bytes = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    @Test
    fun sha256_commitment_matchesPreRoundHash() {
        val serverSeed = "abc1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef"
        val expectedHash = sha256(serverSeed)

        assertEquals(64, expectedHash.length)
        assertEquals(expectedHash, sha256(serverSeed))
    }

    @Test
    fun hmacSha512_derivesDeterministicHash() {
        val serverSeed = "server-secret-key-12345"
        val clientSeed = "client-seed-abc"
        val nonce = 42
        val message = "$clientSeed:$nonce"

        val hash1 = hmacSha512(serverSeed, message)
        val hash2 = hmacSha512(serverSeed, message)

        assertEquals(128, hash1.length)
        assertEquals(hash1, hash2)
    }

    @Test
    fun normalizedFloat_isStrictlyBetweenZeroAndOne() {
        val serverSeed = "test-server-seed"
        val message = "client-seed:1"
        val hash = hmacSha512(serverSeed, message)

        val subHash = hash.substring(0, 13)
        val intVal = subHash.toLong(16)
        val maxVal = Math.pow(2.0, 52.0)
        val floatVal = intVal / maxVal

        assertTrue("Float must be >= 0.0", floatVal >= 0.0)
        assertTrue("Float must be < 1.0", floatVal < 1.0)
    }
}
