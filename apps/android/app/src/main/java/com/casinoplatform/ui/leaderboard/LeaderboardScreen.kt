package com.casinoplatform.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.GoldHighlight
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

data class LeaderboardPlayer(
    val rank: Int,
    val username: String,
    val totalWon: Long,
    val topMultiplier: String,
    val gameTitle: String
)

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier
) {
    var isGlobalTab by remember { mutableStateOf(true) }

    val globalPlayers = listOf(
        LeaderboardPlayer(1, "StarPilot", 845000L, "184.20x", "Rocket Rescue"),
        LeaderboardPlayer(2, "AbyssDiver", 620000L, "92.50x", "Deep Dive"),
        LeaderboardPlayer(3, "NeonFrog", 489000L, "64.00x", "Frog Frenzy"),
        LeaderboardPlayer(4, "MineSweeper", 395000L, "42.10x", "Mines Grid"),
        LeaderboardPlayer(5, "CyberRoller", 290000L, "33.00x", "Cyber Dice")
    )

    val dailyPlayers = listOf(
        LeaderboardPlayer(1, "StarPilot", 92400L, "48.50x", "Rocket Rescue"),
        LeaderboardPlayer(2, "NeonFrog", 61200L, "28.00x", "Frog Frenzy"),
        LeaderboardPlayer(3, "GoldMiner", 45000L, "18.40x", "Mines Grid")
    )

    val players = if (isGlobalTab) globalPlayers else dailyPlayers

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "LEADERBOARDS",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold
        )

        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isGlobalTab) PrimaryGold else DarkSurfaceElevated)
                    .clickable { isGlobalTab = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GLOBAL ALL-TIME",
                    color = if (isGlobalTab) DarkBackground else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isGlobalTab) PrimaryGold else DarkSurfaceElevated)
                    .clickable { isGlobalTab = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TODAY'S LEADERS",
                    color = if (!isGlobalTab) DarkBackground else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Leaderboard List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(players) { p ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface)
                        .border(
                            1.dp,
                            when (p.rank) {
                                1 -> PrimaryGold
                                2 -> GoldHighlight.copy(alpha = 0.5f)
                                3 -> NeonCyan.copy(alpha = 0.5f)
                                else -> DarkSurfaceBorder
                            },
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (p.rank) {
                                            1 -> PrimaryGold
                                            2 -> GoldHighlight
                                            3 -> NeonCyan
                                            else -> DarkSurfaceElevated
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${p.rank}",
                                    color = if (p.rank <= 3) DarkBackground else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(text = p.username, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${p.gameTitle} • ${p.topMultiplier}", color = NeonCyan, fontSize = 11.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format("%,d", p.totalWon),
                                color = NeonEmerald,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "VCOIN", color = PrimaryGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
