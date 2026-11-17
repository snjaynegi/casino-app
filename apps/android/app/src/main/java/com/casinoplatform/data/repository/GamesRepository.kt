package com.casinoplatform.data.repository

import com.casinoplatform.data.model.GameItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamesRepository {
    private val initialGames = listOf(
        GameItem(
            id = "rocket-rescue",
            title = "Rocket Rescue",
            category = "CRASH",
            description = "A rocket travels through an animated space environment. Eject before collision with asteroid fields to bank your multiplied virtual coins!",
            rtp = "97.0%",
            volatility = "MEDIUM",
            activePlayers = 412,
            minBet = 10,
            maxBet = 5000,
            maxMultiplier = "10,000x",
            rules = listOf(
                "Place your virtual coin bet before the launch countdown.",
                "The multiplier increases continuously as the rocket ascends.",
                "Click EJECT at any time to bank your multiplied coins.",
                "Crashing before eject resets the round stake."
            ),
            isFavorite = true,
            isRecentlyPlayed = true
        ),
        GameItem(
            id = "frog-frenzy",
            title = "Frog Frenzy",
            category = "ARCADE",
            description = "Guide your cyber frog across moving neon lily pads with rising hazard risk.",
            rtp = "96.5%",
            volatility = "LOW",
            activePlayers = 189,
            minBet = 10,
            maxBet = 2500,
            maxMultiplier = "500x",
            rules = listOf(
                "Hop forward across moving platforms.",
                "Each successful hop compounds your virtual coin stake.",
                "Cash out anytime or risk for maximum distance multipliers."
            ),
            isRecentlyPlayed = true
        ),
        GameItem(
            id = "deep-dive",
            title = "Deep Dive",
            category = "ARCADE",
            description = "Submerge into the ocean abyss to uncover sunken treasure chests while managing submarine pressure.",
            rtp = "97.2%",
            volatility = "HIGH",
            activePlayers = 143,
            minBet = 20,
            maxBet = 5000,
            maxMultiplier = "2,500x",
            rules = listOf(
                "Navigate deeper oceanic depth tiers.",
                "Unopened chests contain multipliers or kraken hazards."
            )
        ),
        GameItem(
            id = "sky-elevator",
            title = "Sky Elevator",
            category = "ARCADE",
            description = "Ascend a futuristic mega-tower floor by floor with rising floor bonuses.",
            rtp = "96.8%",
            volatility = "HIGH",
            activePlayers = 98,
            minBet = 10,
            maxBet = 3000,
            maxMultiplier = "1,000x",
            rules = listOf("Ride the elevator to higher tiers or cash out banked floors.")
        ),
        GameItem(
            id = "neon-bounce",
            title = "Neon Bounce",
            category = "ARCADE",
            description = "Physics-driven arcade bounce game hitting reactive neon multipliers.",
            rtp = "97.5%",
            volatility = "MEDIUM",
            activePlayers = 112,
            minBet = 10,
            maxBet = 4000,
            maxMultiplier = "750x",
            rules = listOf("Bounce through multipliers and target the jackpot pocket.")
        ),
        GameItem(
            id = "mines-grid",
            title = "Mines Grid",
            category = "INSTANT",
            description = "5x5 grid game with configurable mine density and Provably Fair random generation.",
            rtp = "98.0%",
            volatility = "MEDIUM",
            activePlayers = 215,
            minBet = 10,
            maxBet = 10000,
            maxMultiplier = "5,000x",
            rules = listOf("Uncover diamond tiles and avoid hidden explosive mines.")
        ),
        GameItem(
            id = "plinko-physics",
            title = "Plinko Physics",
            category = "INSTANT",
            description = "Multi-row Galton board with authoritative peg bounce physics.",
            rtp = "97.0%",
            volatility = "MEDIUM",
            activePlayers = 175,
            minBet = 10,
            maxBet = 5000,
            maxMultiplier = "1,000x",
            rules = listOf("Drop balls through pins into outer high multiplier slots.")
        ),
        GameItem(
            id = "cyber-dice",
            title = "Cyber Dice",
            category = "INSTANT",
            description = "Precision dice roll with custom win chance sliders from 1% to 95%.",
            rtp = "98.5%",
            volatility = "LOW",
            activePlayers = 88,
            minBet = 10,
            maxBet = 10000,
            maxMultiplier = "990x",
            rules = listOf("Predict Roll Over or Roll Under with transparent probability.")
        )
    )

    private val _gamesList = MutableStateFlow(initialGames)
    val gamesList: StateFlow<List<GameItem>> = _gamesList.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(setOf("rocket-rescue"))
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    fun toggleFavorite(gameId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(gameId)) {
            current.remove(gameId)
        } else {
            current.add(gameId)
        }
        _favorites.value = current

        _gamesList.value = _gamesList.value.map { g ->
            if (g.id == gameId) g.copy(isFavorite = current.contains(gameId)) else g
        }
    }

    fun getFilteredGames(category: String, query: String): List<GameItem> {
        var list = _gamesList.value

        if (category == "FAVORITES") {
            list = list.filter { _favorites.value.contains(it.id) }
        } else if (category != "ALL") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
            }
        }

        return list
    }

    fun getGameById(id: String): GameItem? {
        return _gamesList.value.find { it.id == id }
    }
}
