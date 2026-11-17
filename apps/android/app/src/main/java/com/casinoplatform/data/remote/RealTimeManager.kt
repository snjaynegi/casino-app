package com.casinoplatform.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class LiveBet(
    val username: String,
    val betAmount: Long,
    val multiplier: Double? = null,
    val isCashedOut: Boolean = false
)

class RealTimeManager {
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _onlineCount = MutableStateFlow(412)
    val onlineCount: StateFlow<Int> = _onlineCount.asStateFlow()

    private val _liveBets = MutableStateFlow<List<LiveBet>>(emptyList())
    val liveBets: StateFlow<List<LiveBet>> = _liveBets.asStateFlow()

    fun connect(authToken: String?) {
        _connectionStatus.value = ConnectionStatus.CONNECTING
        // Establish connection
        _connectionStatus.value = ConnectionStatus.CONNECTED
    }

    fun disconnect() {
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }

    fun onPresenceUpdate(newCount: Int) {
        _onlineCount.value = newCount
    }

    fun addLiveBet(bet: LiveBet) {
        val current = _liveBets.value.toMutableList()
        current.add(0, bet)
        _liveBets.value = current.take(20)
    }

    fun updateBetCashout(username: String, multiplier: Double) {
        _liveBets.value = _liveBets.value.map { b ->
            if (b.username == username) b.copy(multiplier = multiplier, isCashedOut = true) else b
        }
    }
}
