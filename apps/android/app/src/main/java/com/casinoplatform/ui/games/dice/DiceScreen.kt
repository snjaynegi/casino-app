package com.casinoplatform.ui.games.dice

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.audio.CasinoAudioManager
import com.casinoplatform.ui.components.CasinoBetChip
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.WalletBalanceBadge
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.GoldButtonGradient
import com.casinoplatform.ui.theme.NeonCrimson
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

@Composable
fun DiceScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var targetValue by remember { mutableDoubleStateOf(50.0) }
    var isRollOver by remember { mutableStateOf(true) }
    var selectedBet by remember { mutableLongStateOf(100L) }
    var lastRoll by remember { mutableDoubleStateOf(50.00) }
    var isWin by remember { mutableStateOf<Boolean?>(null) }

    val winChance = if (isRollOver) 100.0 - targetValue else targetValue
    val multiplier = Math.floor((98.5 / winChance) * 100) / 100.0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "CYBER DICE", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Provably Fair • 98.5% RTP", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                WalletBalanceBadge(balance = userBalance)
            }

            // Central Roll Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.2f", lastRoll),
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = when (isWin) {
                            true -> NeonEmerald
                            false -> NeonCrimson
                            null -> TextPrimary
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (isWin) {
                            true -> "✓ WINNING ROLL"
                            false -> "✗ MISSED TARGET"
                            null -> "SET TARGET & ROLL"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (isWin) {
                            true -> NeonEmerald
                            false -> NeonCrimson
                            null -> TextSecondary
                        }
                    )
                }
            }

            // Slider & Prediction Controls
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Target slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TARGET: ${targetValue.toInt()}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "WIN CHANCE: ${String.format("%.1f%%", winChance)}", color = NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = targetValue.toFloat(),
                    onValueChange = { targetValue = Math.round(it.toDouble()).toDouble() },
                    valueRange = 5f..95f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryGold,
                        activeTrackColor = PrimaryGold,
                        inactiveTrackColor = DarkSurfaceElevated
                    )
                )

                // Over / Under Toggle Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CasinoBetChip(
                        text = "ROLL OVER (>${targetValue.toInt()})",
                        isSelected = isRollOver,
                        onClick = { isRollOver = true },
                        modifier = Modifier.weight(1f)
                    )
                    CasinoBetChip(
                        text = "ROLL UNDER (<${targetValue.toInt()})",
                        isSelected = !isRollOver,
                        onClick = { isRollOver = false },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Bet Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "SELECT BET (VCOIN):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10L, 50L, 100L, 500L, 1000L).forEach { bet ->
                        CasinoBetChip(
                            text = bet.toString(),
                            isSelected = selectedBet == bet,
                            onClick = { selectedBet = bet },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Main Roll Action
            CasinoPrimaryButton(
                text = "ROLL DICE (${multiplier}x)",
                gradient = GoldButtonGradient,
                enabled = userBalance >= selectedBet,
                onClick = {
                    onBalanceChange(userBalance - selectedBet)
                    val roll = Math.floor(Math.random() * 10000) / 100.0
                    lastRoll = roll
                    val won = if (isRollOver) roll > targetValue else roll < targetValue
                    isWin = won
                    if (won) {
                        CasinoAudioManager.playCashout()
                        val payout = (selectedBet * multiplier).toLong()
                        onBalanceChange(userBalance - selectedBet + payout)
                    } else {
                        CasinoAudioManager.playCrash()
                    }
                }
            )
        }
    }
}
