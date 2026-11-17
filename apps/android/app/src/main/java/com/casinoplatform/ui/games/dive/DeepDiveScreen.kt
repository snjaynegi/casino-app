package com.casinoplatform.ui.games.dive

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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.components.CasinoBetChip
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.CasinoSecondaryButton
import com.casinoplatform.ui.components.WalletBalanceBadge
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary
import kotlin.random.Random

data class DepthTier(val name: String, val depthMeters: Int, val multiplier: Double)

@Composable
fun DeepDiveScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var betAmount by remember { mutableStateOf(100L) }
    var currentTierIdx by remember { mutableIntStateOf(0) } // 0 = Surface, 1..5
    var isDiving by remember { mutableStateOf(false) }
    var isCrushed by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Prepare submarine for deep sea descent.") }

    val tiers = listOf(
        DepthTier("Sunlight Zone", 100, 1.35),
        DepthTier("Twilight Zone", 500, 2.10),
        DepthTier("Midnight Zone", 1500, 3.80),
        DepthTier("Abyssal Plain", 4000, 8.50),
        DepthTier("Hadal Trench", 10000, 25.00)
    )

    val currentMultiplier = if (currentTierIdx > 0 && !isCrushed) tiers[currentTierIdx - 1].multiplier else 1.0
    val currentWin = (betAmount * currentMultiplier).toLong()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceBorder)
                    .clickable(onClick = onBackClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("← LOBBY", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text("DEEP DIVE", color = Color(0xFF00E5FF), fontSize = 18.sp, fontWeight = FontWeight.Black)

            WalletBalanceBadge(balance = userBalance)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Submarine Depth HUD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isCrushed) Color(0xFF381015)
                    else if (isDiving && currentTierIdx > 0) Color(0xFF0A2B42)
                    else DarkSurface
                )
                .border(
                    1.dp,
                    if (isCrushed) Color(0xFFFF3366)
                    else if (isDiving && currentTierIdx > 0) Color(0xFF00E5FF)
                    else DarkSurfaceBorder,
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isCrushed) "HULL PRESSURE BREACH!"
                    else if (isDiving && currentTierIdx > 0) "${tiers[currentTierIdx - 1].depthMeters}M • ${currentMultiplier}x MULTIPLIER"
                    else "SURFACE DOCK (0M)",
                    color = if (isCrushed) Color(0xFFFF5252) else if (isDiving && currentTierIdx > 0) Color(0xFF00E5FF) else PrimaryGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusMessage,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ocean Tiers Elevator List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF041926), Color(0xFF020E17), Color(0xFF000508))
                    )
                )
                .border(1.dp, Color(0xFF103A54), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("OCEANIC DEPTH TIERS", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)

            tiers.reversed().forEachIndexed { revIdx, tier ->
                val tierNum = 5 - revIdx
                val isCurrent = currentTierIdx == tierNum
                val isPassed = currentTierIdx > tierNum

                val bgColor = when {
                    isCurrent && isCrushed -> Color(0xFFFF3366)
                    isCurrent -> Color(0xFF00B0FF)
                    isPassed -> Color(0xFF0077B6)
                    else -> DarkSurfaceElevated.copy(alpha = 0.5f)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .border(1.dp, if (isCurrent) PrimaryGold else DarkSurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = tier.name.uppercase(),
                            color = if (isCurrent) DarkBackground else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${tier.depthMeters} meters",
                            color = if (isCurrent) DarkBackground.copy(alpha = 0.8f) else TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBackground.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${tier.multiplier}x",
                            color = PrimaryGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bet Chips
        if (!isDiving) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(20L, 50L, 100L, 500L).forEach { amount ->
                    CasinoBetChip(
                        text = "$amount",
                        isSelected = betAmount == amount,
                        onClick = { betAmount = amount }
                    )
                }
                CasinoBetChip(
                    text = "MAX",
                    isSelected = betAmount == userBalance.coerceAtMost(5000L),
                    onClick = { betAmount = userBalance.coerceAtMost(5000L) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Controls
        if (!isDiving) {
            CasinoPrimaryButton(
                text = "SUBMERGE & DIVE ($betAmount VCOIN)",
                enabled = userBalance >= betAmount,
                onClick = {
                    if (userBalance < betAmount) return@CasinoPrimaryButton
                    onBalanceChange(userBalance - betAmount)
                    isDiving = true
                    isCrushed = false
                    currentTierIdx = 0
                    statusMessage = "Submarine ready. Dive to Sunlight Zone (100m)!"
                }
            )
        } else if (!isCrushed) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val nextTier = tiers[currentTierIdx]
                CasinoPrimaryButton(
                    text = "DIVE TO ${nextTier.name.uppercase()} (${nextTier.multiplier}x)",
                    onClick = {
                        val nextIdx = currentTierIdx + 1
                        val survived = Random.nextInt(100) < 80 // 80% survival per descent

                        if (survived) {
                            currentTierIdx = nextIdx
                            statusMessage = "Safe descent to ${nextTier.depthMeters}m! Multiplier: ${nextTier.multiplier}x."
                            if (nextIdx == 5) {
                                val win = (betAmount * tiers[4].multiplier).toLong()
                                onBalanceChange(userBalance + win)
                                isDiving = false
                                statusMessage = "DEEP TRENCH CONQUERED! Won +$win VCOIN!"
                            }
                        } else {
                            isCrushed = true
                            isDiving = false
                            statusMessage = "Hull compromised by deep pressure at ${nextTier.depthMeters}m!"
                        }
                    }
                )

                if (currentTierIdx > 0) {
                    CasinoSecondaryButton(
                        text = "SURFACE & BANK (+$currentWin VCOIN)",
                        onClick = {
                            onBalanceChange(userBalance + currentWin)
                            isDiving = false
                            statusMessage = "Safely surfaced! Banked +$currentWin VCOIN at ${currentMultiplier}x!"
                        }
                    )
                }
            }
        } else {
            CasinoPrimaryButton(
                text = "REPAIR SUBMARINE & RETRY",
                onClick = {
                    isCrushed = false
                    isDiving = false
                    currentTierIdx = 0
                    statusMessage = "Submarine repaired. Place bet to dive again!"
                }
            )
        }
    }
}
