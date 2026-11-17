package com.casinoplatform.ui

import com.casinoplatform.ui.components.NavTab
import org.junit.Test
import org.junit.Assert.*

class UiComponentsUnitTest {

    @Test
    fun navTab_hasValidTabsAndIcons() {
        val tabs = NavTab.values()
        assertEquals(5, tabs.size)
        assertEquals("Home", NavTab.HOME.title)
        assertEquals("Games", NavTab.GAMES.title)
        assertEquals("Rewards", NavTab.REWARDS.title)
        assertEquals("Leaders", NavTab.LEADERBOARD.title)
        assertEquals("Profile", NavTab.PROFILE.title)
    }

    @Test
    fun currencyFormatting_formatsCorrectly() {
        val balance = 1250000L
        val formatted = String.format("%,d", balance)
        assertEquals("1,250,000", formatted)
    }

    @Test
    fun multiplierFormatting_handlesDecimalsCorrectly() {
        val multiplier = 3.4218
        val formatted = String.format("%.2fx", multiplier)
        assertEquals("3.42x", formatted)
    }
}
