package com.casinoplatform.games

import com.casinoplatform.data.repository.GamesRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GamesRepositoryUnitTest {

    private lateinit var repository: GamesRepository

    @Before
    fun setUp() {
        repository = GamesRepository()
    }

    @Test
    fun gamesList_containsAllCatalogueTitles() {
        val games = repository.gamesList.value
        assertTrue(games.size >= 8)

        val rocket = games.find { it.id == "rocket-rescue" }
        assertNotNull(rocket)
        assertEquals("CRASH", rocket?.category)
        assertEquals("97.0%", rocket?.rtp)

        val frog = games.find { it.id == "frog-frenzy" }
        assertNotNull(frog)
        assertEquals("ARCADE", frog?.category)
    }

    @Test
    fun filtering_byCategory_returnsCorrectSubset() {
        val crashGames = repository.getFilteredGames("CRASH", "")
        assertTrue(crashGames.isNotEmpty())
        assertTrue(crashGames.all { it.category == "CRASH" })

        val arcadeGames = repository.getFilteredGames("ARCADE", "")
        assertTrue(arcadeGames.isNotEmpty())
        assertTrue(arcadeGames.all { it.category == "ARCADE" })
    }

    @Test
    fun filtering_bySearchQuery_findsMatchingTitles() {
        val searchMines = repository.getFilteredGames("ALL", "mines")
        assertEquals(1, searchMines.size)
        assertEquals("mines-grid", searchMines[0].id)
    }

    @Test
    fun favorites_toggle_updatesStateAndFilters() {
        val gameId = "deep-dive"
        assertFalse(repository.favorites.value.contains(gameId))

        repository.toggleFavorite(gameId)
        assertTrue(repository.favorites.value.contains(gameId))

        val favs = repository.getFilteredGames("FAVORITES", "")
        assertTrue(favs.any { it.id == gameId })

        repository.toggleFavorite(gameId)
        assertFalse(repository.favorites.value.contains(gameId))
    }
}
