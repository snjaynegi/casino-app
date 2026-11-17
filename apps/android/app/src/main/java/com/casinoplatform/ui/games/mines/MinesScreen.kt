package com.casinoplatform.ui.games.mines

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.casinoplatform.ui.theme.PurpleButtonGradient
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

enum class MinesTileState {
    HIDDEN,
    GEM,
    MINE
}

@Composable
fun MinesScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRoundActive by remember { mutableStateOf(false) }
    var selectedBet by remember { mutableLongStateOf(100L) }
    var selectedMinesCount by remember { mutableIntStateOf(3) }
    var currentMultiplier by remember { mutableDoubleStateOf(1.0) }
    var gemsFound by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var isWon by remember { mutableStateOf(false) }

    // Grid states (25 tiles)
    val tileStates = remember { mutableStateOf(List(25) { MinesTileState.HIDDEN }) }
    val mineIndices = remember { mutableStateOf(setOf<Int>()) }

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
                        Text(text = "MINES GRID", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Instant Win • 98.0% RTP", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                WalletBalanceBadge(balance = userBalance)
            }

            // Status Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MINES", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "$selectedMinesCount / 25", color = NeonCrimson, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "GEMS FOUND", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "$gemsFound", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MULTIPLIER", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = String.format("%.2fx", currentMultiplier),
                            color = if (isGameOver && !isWon) NeonCrimson else PrimaryGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 5x5 Mines Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(25) { index ->
                    val state = tileStates.value[index]
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (state) {
                                    MinesTileState.HIDDEN -> DarkSurfaceElevated
                                    MinesTileState.GEM -> NeonEmerald.copy(alpha = 0.25f)
                                    MinesTileState.MINE -> NeonCrimson.copy(alpha = 0.3f)
                                }
                            )
                            .border(
                                1.dp,
                                when (state) {
                                    MinesTileState.HIDDEN -> DarkSurfaceBorder
                                    MinesTileState.GEM -> NeonEmerald
                                    MinesTileState.MINE -> NeonCrimson
                                },
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(enabled = isRoundActive && state == MinesTileState.HIDDEN) {
                                val currentMines = mineIndices.value
                                val newStates = tileStates.value.toMutableList()

                                if (currentMines.contains(index)) {
                                    // Hit mine -> Round Over
                                    newStates[index] = MinesTileState.MINE
                                    // Reveal all remaining mines
                                    currentMines.forEach { m -> newStates[m] = MinesTileState.MINE }
                                    tileStates.value = newStates
                                    isRoundActive = false
                                    isGameOver = true
                                    isWon = false
                                    CasinoAudioManager.playCrash()
                                } else {
                                    // Found gem
                                    newStates[index] = MinesTileState.GEM
                                    tileStates.value = newStates
                                    gemsFound++
                                    // Approximate multiplier progression
                                    currentMultiplier = 1.0 + (gemsFound * 0.35)
                                    CasinoAudioManager.playGemReveal()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (state) {
                                MinesTileState.HIDDEN -> ""
                                MinesTileState.GEM -> "💎"
                                MinesTileState.MINE -> "💣"
                            },
                            fontSize = 20.sp
                        )
                    }
                }
            }

            // Bet & Mine Selector Row
            if (!isRoundActive) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "MINES DENSITY:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 3, 5, 10, 15).forEach { count ->
                            CasinoBetChip(
                                text = "$count",
                                isSelected = selectedMinesCount == count,
                                onClick = { selectedMinesCount = count },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Main Action Button
            if (!isRoundActive) {
                CasinoPrimaryButton(
                    text = "START MINES ROUND ($selectedBet VCOIN)",
                    gradient = GoldButtonGradient,
                    enabled = userBalance >= selectedBet,
                    onClick = {
                        onBalanceChange(userBalance - selectedBet)
                        // Randomly place selectedMinesCount
                        val allIndices = (0..24).toList().shuffled()
                        mineIndices.value = allIndices.take(selectedMinesCount).toSet()
                        tileStates.value = List(25) { MinesTileState.HIDDEN }
                        gemsFound = 0
                        currentMultiplier = 1.0
                        isGameOver = false
                        isWon = false
                        isRoundActive = true
                    }
                )
            } else {
                val cashoutAmount = (selectedBet * currentMultiplier).toLong()
                CasinoPrimaryButton(
                    text = "CASH OUT ($cashoutAmount VCOIN)",
                    gradient = PurpleButtonGradient,
                    textColor = TextPrimary,
                    enabled = gemsFound > 0,
                    onClick = {
                        CasinoAudioManager.playCashout()
                        onBalanceChange(userBalance + cashoutAmount)
                        isRoundActive = false
                        isGameOver = true
                        isWon = true
                    }
                )
            }
        }
    }
}
