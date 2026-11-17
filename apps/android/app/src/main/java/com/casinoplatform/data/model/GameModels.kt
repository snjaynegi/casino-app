package com.casinoplatform.data.model

data class GameItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val rtp: String,
    val volatility: String,
    val activePlayers: Int,
    val minBet: Long,
    val maxBet: Long,
    val maxMultiplier: String,
    val rules: List<String>,
    val isFavorite: Boolean = false,
    val isRecentlyPlayed: Boolean = false
)
