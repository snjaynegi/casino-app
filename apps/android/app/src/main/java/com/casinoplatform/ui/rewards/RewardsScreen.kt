package com.casinoplatform.ui.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.WalletBalanceBadge
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.GoldButtonGradient
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

@Composable
fun RewardsScreen(
    userBalance: Long,
    onClaimReward: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var streakDay by remember { mutableIntStateOf(4) }
    var hasClaimedToday by remember { mutableStateOf(false) }

    val streakRewards = listOf(500L, 750L, 1000L, 1250L, 1500L, 2000L, 3000L)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DAILY REWARDS & MISSIONS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGold
                )
                Text(
                    text = "Free Virtual Coins Every 24 Hours",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            WalletBalanceBadge(balance = userBalance)
        }

        // 7-Day Login Streak Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "7-DAY STREAK BONUS",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Day $streakDay of 7",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 7 Days Pill Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    streakRewards.forEachIndexed { idx, reward ->
                        val dayNum = idx + 1
                        val isDone = dayNum < streakDay || (dayNum == streakDay && hasClaimedToday)
                        val isCurrent = dayNum == streakDay && !hasClaimedToday

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> NeonEmerald.copy(alpha = 0.2f)
                                            isCurrent -> PrimaryGold
                                            else -> DarkSurfaceElevated
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when {
                                            isDone -> NeonEmerald
                                            isCurrent -> PrimaryGold
                                            else -> DarkSurfaceBorder
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isDone) "✓" else "D$dayNum",
                                    color = if (isCurrent) DarkBackground else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "+${reward}",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                CasinoPrimaryButton(
                    text = if (hasClaimedToday) "CLAIMED TODAY • COME BACK IN 18H" else "CLAIM DAY $streakDay (+${streakRewards[streakDay - 1]} VCOIN)",
                    gradient = GoldButtonGradient,
                    enabled = !hasClaimedToday,
                    onClick = {
                        val reward = streakRewards[streakDay - 1]
                        onClaimReward(reward)
                        hasClaimedToday = true
                    }
                )
            }
        }

        // Daily Missions List
        Text(
            text = "ACTIVE DAILY MISSIONS",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold
        )

        listOf(
            Triple("Flight Veteran", "Complete 5 flights in Rocket Rescue", "+500 VCOIN"),
            Triple("Diamond Seeker", "Find 10 gems in Mines Grid", "+750 VCOIN"),
            Triple("Over & Under", "Win 3 rounds in Cyber Dice", "+400 VCOIN")
        ).forEach { (title, desc, reward) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = desc, color = TextSecondary, fontSize = 12.sp)
                    }
                    Text(text = reward, color = NeonEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
