package com.casinoplatform.ui.games.frog

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.audio.CasinoAudioManager
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

@Composable
fun FrogFrenzyScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var betAmount by remember { mutableStateOf(100L) }
    var currentStep by remember { mutableIntStateOf(0) } // 0 = start, 1..6
    var isPlaying by remember { mutableStateOf(false) }
    var isSunk by remember { mutableStateOf(false) }
    var gameMessage by remember { mutableStateOf("Place bet and hop forward across lily pads!") }

    val padMultipliers = listOf(1.25, 1.70, 2.50, 4.00, 7.50, 15.00)

    val currentMultiplier = if (currentStep > 0 && !isSunk) padMultipliers[currentStep - 1] else 1.0
    val potentialWin = (betAmount * currentMultiplier).toLong()

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

            Text("FROG FRENZY", color = Color(0xFF00F5A0), fontSize = 18.sp, fontWeight = FontWeight.Black)

            WalletBalanceBadge(balance = userBalance)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Multiplier & Status Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isSunk) Color(0xFF38151A)
                    else if (isPlaying && currentStep > 0) Color(0xFF0E382A)
                    else DarkSurface
                )
                .border(
                    1.dp,
                    if (isSunk) Color(0xFFFF3366)
                    else if (isPlaying && currentStep > 0) Color(0xFF00F5A0)
                    else DarkSurfaceBorder,
                    RoundedCornerShape(14.dp)
                )
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isSunk) "SPLASH! SANK IN THE POND"
                    else if (isPlaying && currentStep > 0) "SAFE! ${currentMultiplier}x MULTIPLIER"
                    else "READY TO HOP",
                    color = if (isSunk) Color(0xFFFF5252) else if (isPlaying && currentStep > 0) Color(0xFF00F5A0) else PrimaryGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = gameMessage,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // River Pads Arena
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF041913), Color(0xFF0B2E24), Color(0xFF03140F))
                    )
                )
                .border(1.dp, Color(0xFF1B4E3E), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("POND CROSSING (6 PADS)", color = Color(0xFF00F5A0), fontSize = 11.sp, fontWeight = FontWeight.Bold)

            padMultipliers.reversed().forEachIndexed { reverseIdx, mult ->
                val padIndex = 6 - reverseIdx // 6 down to 1
                val isCurrentPad = currentStep == padIndex
                val isPassed = currentStep > padIndex
                val isUpcoming = currentStep < padIndex

                val padColor = when {
                    isCurrentPad && isSunk -> Color(0xFFFF3366)
                    isCurrentPad -> Color(0xFF00F5A0)
                    isPassed -> Color(0xFF00A86B)
                    else -> DarkSurfaceElevated
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(padColor.copy(alpha = if (isUpcoming) 0.5f else 0.9f))
                        .border(
                            1.dp,
                            if (isCurrentPad) PrimaryGold else DarkSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isCurrentPad && !isSunk) "FROG HERE"
                            else if (isCurrentPad && isSunk) "SUNK"
                            else if (isPassed) "CLEARED"
                            else "PAD #$padIndex",
                            color = if (isCurrentPad) DarkBackground else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBackground.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${mult}x",
                            color = PrimaryGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bet Amount Selector
        if (!isPlaying) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(10L, 50L, 100L, 500L).forEach { amount ->
                    CasinoBetChip(
                        text = "$amount",
                        isSelected = betAmount == amount,
                        onClick = { betAmount = amount }
                    )
                }
                CasinoBetChip(
                    text = "MAX",
                    isSelected = betAmount == userBalance.coerceAtMost(2500L),
                    onClick = { betAmount = userBalance.coerceAtMost(2500L) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Buttons
        if (!isPlaying) {
            CasinoPrimaryButton(
                text = "START HOPPING ($betAmount VCOIN)",
                enabled = userBalance >= betAmount,
                onClick = {
                    if (userBalance < betAmount) return@CasinoPrimaryButton
                    onBalanceChange(userBalance - betAmount)
                    isPlaying = true
                    isSunk = false
                    currentStep = 0
                    gameMessage = "Click HOP to leap onto Pad #1!"
                }
            )
        } else if (!isSunk) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Hop Button
                CasinoPrimaryButton(
                    text = "HOP TO PAD #${currentStep + 1} (${padMultipliers[currentStep]}x)",
                    onClick = {
                        val nextStep = currentStep + 1
                        // 82% survival probability per step
                        val survived = Random.nextInt(100) < 82

                        if (survived) {
                            currentStep = nextStep
                            gameMessage = "Safe landing on Pad #$nextStep! Multiplier is now ${padMultipliers[nextStep - 1]}x."
                            if (nextStep == 6) {
                                // Reached the golden jackpot pad!
                                CasinoAudioManager.playCashout()
                                val win = (betAmount * padMultipliers[5]).toLong()
                                onBalanceChange(userBalance + win)
                                isPlaying = false
                                gameMessage = "JACKPOT! Cleared all 6 pads! Won +$win VCOIN!"
                            } else {
                                CasinoAudioManager.playHop()
                            }
                        } else {
                            CasinoAudioManager.playCrash()
                            isSunk = true
                            isPlaying = false
                            gameMessage = "Lily pad collapsed! You slipped into the pond."
                        }
                    }
                )

                // Cashout Button
                if (currentStep > 0) {
                    CasinoSecondaryButton(
                        text = "CASH OUT NOW (+${potentialWin} VCOIN)",
                        onClick = {
                            CasinoAudioManager.playCashout()
                            onBalanceChange(userBalance + potentialWin)
                            isPlaying = false
                            gameMessage = "Successfully banked +$potentialWin VCOIN at ${currentMultiplier}x!"
                        }
                    )
                }
            }
        } else {
            CasinoPrimaryButton(
                text = "PLAY AGAIN",
                onClick = {
                    isSunk = false
                    isPlaying = false
                    currentStep = 0
                    gameMessage = "Place bet to try again!"
                }
            )
        }
    }
}
