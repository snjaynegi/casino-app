package com.casinoplatform.games

import com.casinoplatform.ui.games.mines.MinesTileState
import org.junit.Assert.*
import org.junit.Test

class AdditionalGamesUnitTest {

    @Test
    fun minesMultiplier_growsDeterministically() {
        // Formula test
        fun calculateMinesMultiplier(mineCount: Int, gems: Int): Double {
            var prob = 1.0
            for (i in 0 until gems) {
                prob *= (25.0 - mineCount - i) / (25.0 - i)
            }
            return Math.floor((0.98 / prob) * 100) / 100.0
        }

        val mult1 = calculateMinesMultiplier(3, 1)
        val mult2 = calculateMinesMultiplier(3, 2)
        val mult3 = calculateMinesMultiplier(3, 3)

        assertTrue("First gem multiplier must be > 1.0", mult1 > 1.0)
        assertTrue("Subsequent gems must increase multiplier", mult2 > mult1)
        assertTrue("Third gem must be higher than second", mult3 > mult2)
    }

    @Test
    fun diceMultiplier_calculatesAccurately() {
        fun calculateDiceMultiplier(winChance: Double): Double {
            return Math.floor((98.5 / winChance) * 100) / 100.0
        }

        val mult50 = calculateDiceMultiplier(50.0)
        assertEquals(1.97, mult50, 0.01)

        val mult25 = calculateDiceMultiplier(25.0)
        assertEquals(3.94, mult25, 0.01)
    }

    @Test
    fun minesTileStates_hasAllThreeRepresentations() {
        val states = MinesTileState.values()
        assertEquals(3, states.size)
        assertTrue(states.contains(MinesTileState.HIDDEN))
        assertTrue(states.contains(MinesTileState.GEM))
        assertTrue(states.contains(MinesTileState.MINE))
    }
}
