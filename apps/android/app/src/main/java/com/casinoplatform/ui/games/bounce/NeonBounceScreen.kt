package com.casinoplatform.ui.games.bounce

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun NeonBounceScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var betAmount by remember { mutableStateOf(100L) }
    var isBouncing by remember { mutableStateOf(false) }
    var bumperHits by remember { mutableIntStateOf(0) }
    var lastMultiplier by remember { mutableStateOf<Double?>(null) }
    var lastWin by remember { mutableStateOf<Long?>(null) }
    var statusText by remember { mutableStateOf("Launch the neon ball into the bumper pinfield!") }

    val ballX = remember { Animatable(0.5f) }
    val ballY = remember { Animatable(0.9f) }
    val scope = rememberCoroutineScope()

    val pockets = listOf(1.5, 3.0, 8.0, 3.0, 1.5)

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

            Text("NEON BOUNCE", color = Color(0xFFFF3366), fontSize = 18.sp, fontWeight = FontWeight.Black)

            WalletBalanceBadge(balance = userBalance)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // HUD Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface)
                .border(1.dp, Color(0xFFFF3366).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (lastMultiplier != null) "${lastMultiplier}x MULTIPLIER! (+$lastWin VCOIN)"
                    else if (isBouncing) "BOUNCING! $bumperHits BUMPERS HIT"
                    else "ARCADE PINFIELD",
                    color = if (lastMultiplier != null) PrimaryGold else Color(0xFFFF3366),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = statusText, color = TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Arcade Pinfield Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E0729), Color(0xFF12031B), Color(0xFF09010E))
                    )
                )
                .border(1.dp, Color(0xFF4A1061), RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Reactive Bumpers
                val bumpers = listOf(
                    Offset(w * 0.28f, h * 0.28f) to Color(0xFFFF3366),
                    Offset(w * 0.72f, h * 0.28f) to Color(0xFF00F5A0),
                    Offset(w * 0.50f, h * 0.45f) to PrimaryGold,
                    Offset(w * 0.30f, h * 0.62f) to Color(0xFF00E5FF),
                    Offset(w * 0.70f, h * 0.62f) to Color(0xFFFF3366)
                )

                bumpers.forEach { (pos, color) ->
                    drawCircle(color.copy(alpha = 0.25f), radius = 22.dp.toPx(), center = pos)
                    drawCircle(color, radius = 14.dp.toPx(), center = pos)
                    drawCircle(Color.White, radius = 5.dp.toPx(), center = pos)
                }

                // Falling/Bouncing Neon Ball
                if (isBouncing) {
                    val currX = w * ballX.value
                    val currY = h * ballY.value
                    drawCircle(PrimaryGold.copy(alpha = 0.4f), radius = 14.dp.toPx(), center = Offset(currX, currY))
                    drawCircle(PrimaryGold, radius = 8.dp.toPx(), center = Offset(currX, currY))
                    drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(currX - 2.dp.toPx(), currY - 2.dp.toPx()))
                }
            }

            // Bottom Multiplier Pockets
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                pockets.forEach { pocketMult ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E0C40))
                            .border(1.dp, PrimaryGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${pocketMult}x",
                            color = PrimaryGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bet Chips
        if (!isBouncing) {
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
                    isSelected = betAmount == userBalance.coerceAtMost(4000L),
                    onClick = { betAmount = userBalance.coerceAtMost(4000L) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Launch Action Button
        CasinoPrimaryButton(
            text = if (isBouncing) "BOUNCING IN PINFIELD..." else "LAUNCH BALL ($betAmount VCOIN)",
            enabled = !isBouncing && userBalance >= betAmount,
            onClick = {
                if (userBalance < betAmount) return@CasinoPrimaryButton
                onBalanceChange(userBalance - betAmount)
                isBouncing = true
                bumperHits = 0
                lastMultiplier = null
                lastWin = null
                statusText = "Ball launched! Bouncing across bumpers..."

                scope.launch {
                    ballX.snapTo(0.5f)
                    ballY.snapTo(0.9f)

                    // Launch upward into upper bumpers
                    ballY.animateTo(0.2f, tween(250))
                    ballX.animateTo(0.3f, tween(200))
                    bumperHits++
                    CasinoAudioManager.playBounce()
                    delay(80)

                    // Bumper ricochet 1
                    ballX.animateTo(0.7f, tween(180))
                    ballY.animateTo(0.35f, tween(180))
                    bumperHits++
                    CasinoAudioManager.playBounce()
                    delay(80)

                    // Bumper ricochet 2
                    ballX.animateTo(0.48f, tween(160))
                    ballY.animateTo(0.50f, tween(160))
                    bumperHits++
                    CasinoAudioManager.playBounce()
                    delay(80)

                    // Bumper ricochet 3
                    ballX.animateTo(0.68f, tween(160))
                    ballY.animateTo(0.65f, tween(160))
                    bumperHits++
                    CasinoAudioManager.playBounce()
                    delay(80)

                    // Fall into bottom pocket (0..4)
                    val pocketIdx = Random.nextInt(pockets.size)
                    val pocketXPositions = listOf(0.12f, 0.30f, 0.50f, 0.70f, 0.88f)
                    ballX.animateTo(pocketXPositions[pocketIdx], tween(180))
                    ballY.animateTo(0.92f, tween(180))

                    val pocketMult = pockets[pocketIdx]
                    val bumperBonus = bumperHits * 0.2
                    val totalMultiplier = Math.round((pocketMult + bumperBonus) * 100.0) / 100.0
                    val win = (betAmount * totalMultiplier).toLong()

                    if (totalMultiplier >= 1.0) {
                        CasinoAudioManager.playCashout()
                    } else {
                        CasinoAudioManager.playCrash()
                    }

                    lastMultiplier = totalMultiplier
                    lastWin = win
                    onBalanceChange(userBalance - betAmount + win)
                    statusText = "Pocket ${pocketMult}x + $bumperHits Bumper Hits = ${totalMultiplier}x! Won +$win VCOIN!"

                    delay(300)
                    isBouncing = false
                }
            }
        )
    }
}
