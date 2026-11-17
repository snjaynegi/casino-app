package com.casinoplatform.games

import com.casinoplatform.ui.games.rocketrescue.FlightState
import org.junit.Assert.*
import org.junit.Test

class RocketRescueUnitTest {

    private fun calculateMultiplier(elapsedSeconds: Double): Double {
        if (elapsedSeconds <= 0.0) return 1.00
        val raw = 1.0 + 0.06 * Math.pow(elapsedSeconds, 1.45)
        return Math.floor(raw * 100) / 100.0
    }

    @Test
    fun flightMultiplier_startsAtOne() {
        val initial = calculateMultiplier(0.0)
        assertEquals(1.00, initial, 0.001)
    }

    @Test
    fun flightMultiplier_growsMonotonically() {
        var previous = 1.00
        for (sec in 1..20) {
            val current = calculateMultiplier(sec.toDouble())
            assertTrue("Multiplier at $sec must be > $previous", current > previous)
            previous = current
        }
    }

    @Test
    fun flightStates_coverAllTransitions() {
        val states = FlightState.values()
        assertEquals(4, states.size)
        assertTrue(states.contains(FlightState.IDLE))
        assertTrue(states.contains(FlightState.FLYING))
        assertTrue(states.contains(FlightState.CASHED_OUT))
        assertTrue(states.contains(FlightState.CRASHED))
    }
}
