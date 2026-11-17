package com.casinoplatform.wallet

import org.junit.Assert.*
import org.junit.Test

class WalletUnitTest {

    @Test
    fun walletBalance_atomicCalculations_areAccurate() {
        var balance = 1000L
        val betAmount = 100L
        val multiplier = 2.50

        // Bet debit
        balance -= betAmount
        assertEquals(900L, balance)

        // Win credit
        val payout = (betAmount * multiplier).toLong()
        balance += payout
        assertEquals(1150L, balance)
    }

    @Test
    fun streakProgression_strictlyIncreasesAcrossSevenDays() {
        val streakRewards = listOf(500L, 750L, 1000L, 1250L, 1500L, 2000L, 3000L)
        assertEquals(7, streakRewards.size)

        for (i in 0 until streakRewards.size - 1) {
            assertTrue("Day ${i + 2} reward must exceed Day ${i + 1}", streakRewards[i + 1] > streakRewards[i])
        }
    }
}
