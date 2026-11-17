package com.casinoplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.data.model.GameItem
import com.casinoplatform.data.repository.GamesRepository
import com.casinoplatform.ui.components.CasinoBottomBar
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.CasinoTopAppBar
import com.casinoplatform.ui.components.GameCard
import com.casinoplatform.ui.components.NavTab
import com.casinoplatform.ui.components.StatCard
import com.casinoplatform.ui.games.dice.DiceScreen
import com.casinoplatform.ui.games.mines.MinesScreen
import com.casinoplatform.ui.games.rocketrescue.RocketRescueScreen
import com.casinoplatform.ui.leaderboard.LeaderboardScreen
import com.casinoplatform.ui.lobby.GameDetailDialog
import com.casinoplatform.ui.lobby.GameLobbyScreen
import com.casinoplatform.ui.profile.ProfileScreen
import com.casinoplatform.ui.rewards.RewardsScreen
import com.casinoplatform.ui.theme.CasinoPlatformTheme
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CasinoPlatformTheme {
                MainAppScaffold()
            }
        }
    }
}

@Composable
fun MainAppScaffold() {
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var userBalance by remember { mutableStateOf(10000L) }
    var selectedGameDetail by remember { mutableStateOf<GameItem?>(null) }
    var activeGameId by remember { mutableStateOf<String?>(null) }
    val gamesRepo = remember { GamesRepository() }

    // Route active game screens
    when (activeGameId) {
        "rocket-rescue" -> {
            RocketRescueScreen(
                userBalance = userBalance,
                onBalanceChange = { userBalance = it },
                onBackClick = { activeGameId = null }
            )
            return
        }
        "mines-grid" -> {
            MinesScreen(
                userBalance = userBalance,
                onBalanceChange = { userBalance = it },
                onBackClick = { activeGameId = null }
            )
            return
        }
        "cyber-dice" -> {
            DiceScreen(
                userBalance = userBalance,
                onBalanceChange = { userBalance = it },
                onBackClick = { activeGameId = null }
            )
            return
        }
    }

    Scaffold(
        topBar = {
            CasinoTopAppBar(
                username = "NeonPilot",
                level = 12,
                balance = userBalance,
                onBalanceClick = { userBalance += 500L }
            )
        },
        bottomBar = {
            CasinoBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                NavTab.HOME -> {
                    HomeScreenContent(
                        onSelectGame = { selectedGameDetail = it },
                        onClaimDaily = { userBalance += 500L }
                    )
                }
                NavTab.GAMES -> {
                    GameLobbyScreen(
                        repository = gamesRepo,
                        onSelectGame = { selectedGameDetail = it }
                    )
                }
                NavTab.REWARDS -> {
                    RewardsScreen(
                        userBalance = userBalance,
                        onClaimReward = { userBalance += it }
                    )
                }
                NavTab.LEADERBOARD -> {
                    LeaderboardScreen()
                }
                NavTab.PROFILE -> {
                    ProfileScreen(
                        username = "NeonPilot",
                        balance = userBalance,
                        onSignOut = { currentTab = NavTab.HOME }
                    )
                }
            }

            // Game Detail Sheet / Dialog
            selectedGameDetail?.let { game ->
                GameDetailDialog(
                    game = game,
                    isFavorite = gamesRepo.favorites.value.contains(game.id),
                    onToggleFavorite = { gamesRepo.toggleFavorite(it) },
                    onLaunchGame = {
                        activeGameId = game.id
                        selectedGameDetail = null
                    },
                    onDismiss = { selectedGameDetail = null }
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    onSelectGame: (GameItem) -> Unit,
    onClaimDaily: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Featured Gaming Lobby",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Win Rate",
                    value = "64.2%",
                    subtitle = "Last 50 rounds",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Top Multiplier",
                    value = "48.50x",
                    subtitle = "Rocket Rescue",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Popular Instant Games",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            GameCard(
                title = "Rocket Rescue",
                category = "Crash",
                rtp = "97.0%",
                activePlayers = 412,
                onClick = {
                    onSelectGame(
                        GameItem(
                            id = "rocket-rescue",
                            title = "Rocket Rescue",
                            category = "CRASH",
                            description = "Ascend through space, dodge asteroid hazards and eject before the crash!",
                            rtp = "97.0%",
                            volatility = "MEDIUM",
                            activePlayers = 412,
                            minBet = 10,
                            maxBet = 5000,
                            maxMultiplier = "10,000x",
                            rules = listOf("Place bet", "Eject before crash")
                        )
                    )
                }
            )
        }

        item {
            GameCard(
                title = "Mines Grid",
                category = "Instant",
                rtp = "98.0%",
                activePlayers = 215,
                onClick = {
                    onSelectGame(
                        GameItem(
                            id = "mines-grid",
                            title = "Mines Grid",
                            category = "INSTANT",
                            description = "Uncover diamond gems across 25 tiles and dodge hidden mines.",
                            rtp = "98.0%",
                            volatility = "MEDIUM",
                            activePlayers = 215,
                            minBet = 10,
                            maxBet = 10000,
                            maxMultiplier = "5,000x",
                            rules = listOf("Pick mine density (1-24)", "Uncover gems to multiply bet", "Cash out anytime")
                        )
                    )
                }
            )
        }

        item {
            GameCard(
                title = "Cyber Dice",
                category = "Instant",
                rtp = "98.5%",
                activePlayers = 88,
                onClick = {
                    onSelectGame(
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
                            rules = listOf("Set roll target", "Predict Over or Under", "Win instant multiplied coins")
                        )
                    )
                }
            )
        }

        item {
            CasinoPrimaryButton(
                text = "CLAIM DAILY REWARD (+500 VCOIN)",
                onClick = onClaimDaily
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
