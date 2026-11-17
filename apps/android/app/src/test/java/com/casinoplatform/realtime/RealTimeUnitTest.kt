package com.casinoplatform.realtime

import com.casinoplatform.data.remote.ConnectionStatus
import com.casinoplatform.data.remote.LiveBet
import com.casinoplatform.data.remote.RealTimeManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RealTimeUnitTest {

    private lateinit var realTimeManager: RealTimeManager

    @Before
    fun setUp() {
        realTimeManager = RealTimeManager()
    }

    @Test
    fun connectionStatus_transitionsToConnected() {
        assertEquals(ConnectionStatus.DISCONNECTED, realTimeManager.connectionStatus.value)

        realTimeManager.connect("test-jwt")
        assertEquals(ConnectionStatus.CONNECTED, realTimeManager.connectionStatus.value)

        realTimeManager.disconnect()
        assertEquals(ConnectionStatus.DISCONNECTED, realTimeManager.connectionStatus.value)
    }

    @Test
    fun presence_updatesCorrectly() {
        realTimeManager.onPresenceUpdate(580)
        assertEquals(580, realTimeManager.onlineCount.value)
    }

    @Test
    fun liveBets_tracksAndUpdatesCashouts() {
        val bet = LiveBet(username = "Pilot99", betAmount = 250L)
        realTimeManager.addLiveBet(bet)

        val currentBets = realTimeManager.liveBets.value
        assertEquals(1, currentBets.size)
        assertEquals("Pilot99", currentBets[0].username)
        assertFalse(currentBets[0].isCashedOut)

        realTimeManager.updateBetCashout("Pilot99", 3.42)
        val updated = realTimeManager.liveBets.value[0]
        assertTrue(updated.isCashedOut)
        assertEquals(3.42, updated.multiplier ?: 0.0, 0.001)
    }
}
