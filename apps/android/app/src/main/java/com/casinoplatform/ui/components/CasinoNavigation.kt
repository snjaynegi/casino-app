package com.casinoplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.audio.CasinoAudioManager
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextMuted
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

enum class NavTab(val title: String, val icon: String) {
    HOME("Home", "🏠"),
    GAMES("Games", "🎮"),
    REWARDS("Rewards", "🎁"),
    LEADERBOARD("Leaders", "🏆"),
    PROFILE("Profile", "👤")
}

@Composable
fun CasinoBottomBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(DarkSurface)
            .border(width = 1.dp, color = DarkSurfaceBorder)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable {
                            CasinoAudioManager.playClick()
                            onTabSelected(tab)
                        }
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = tab.icon,
                        fontSize = if (isSelected) 22.sp else 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        color = if (isSelected) PrimaryGold else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun CasinoTopAppBar(
    username: String,
    level: Int,
    balance: Long,
    onBalanceClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player avatar & tag
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
                    .border(1.5.dp, PrimaryGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.take(1).uppercase(),
                    color = PrimaryGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = username,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Level $level",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Balance & Notification action
        Row(verticalAlignment = Alignment.CenterVertically) {
            WalletBalanceBadge(
                balance = balance,
                onClick = onBalanceClick
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Sound Effects Mute Toggle
            val isMuted by CasinoAudioManager.isMuted.collectAsState()
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
                    .clickable {
                        CasinoAudioManager.toggleMute()
                        if (isMuted) {
                            // If it was muted, now unmuted: play a sample click tone
                            CasinoAudioManager.playClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isMuted) "🔇" else "🔊",
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
                    .clickable {
                        CasinoAudioManager.playClick()
                        onNotificationClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔔", fontSize = 15.sp)
            }
        }
    }
}
