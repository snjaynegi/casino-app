package com.casinoplatform.ui.games.plinko

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.components.CasinoBetChip
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.WalletBalanceBadge
import com.casinoplatform.ui.fairness.ProvablyFairDialog
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun PlinkoScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var betAmount by remember { mutableStateOf(100L) }
    var riskLevel by remember { mutableStateOf("MEDIUM") }
    var rowCount by remember { mutableIntStateOf(8) }
    var isDropping by remember { mutableStateOf(false) }
    var activeSlotIndex by remember { mutableStateOf<Int?>(null) }
    var lastWinAmount by remember { mutableStateOf<Long?>(null) }
    var lastMultiplier by remember { mutableStateOf<Double?>(null) }
    var showFairnessDialog by remember { mutableStateOf(false) }

    val ballY = remember { Animatable(0f) }
    val ballX = remember { Animatable(0.5f) }
    val scope = rememberCoroutineScope()

    // Multipliers for 8 rows (9 slots)
    val multipliers = when (riskLevel) {
        "LOW" -> listOf(5.6, 2.1, 1.1, 1.0, 0.5, 1.0, 1.1, 2.1, 5.6)
        "HIGH" -> listOf(29.0, 4.0, 1.5, 0.3, 0.2, 0.3, 1.5, 4.0, 29.0)
        else -> listOf(13.0, 3.0, 1.3, 0.7, 0.4, 0.7, 1.3, 3.0, 13.0)
    }

    if (showFairnessDialog) {
        ProvablyFairDialog(
            roundId = "PLINKO-42",
            multiplier = lastMultiplier ?: 1.0,
            serverSeed = "9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b",
            serverSeedHash = "f9a4e7230b561cda45e09871bbec63d098e765412fa09871cd432a10be56f123",
            clientSeed = "plinko-client-seed-1",
            nonce = 42L,
            onDismiss = { showFairnessDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
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

            Text("PLINKO PHYSICS", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Black)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .clickable { showFairnessDialog = true }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text("FAIR", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Balance & Win Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WalletBalanceBadge(balance = userBalance)

            if (lastWinAmount != null && lastMultiplier != null) {
                val isWin = lastMultiplier!! >= 1.0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isWin) Color(0xFF1B382A) else Color(0xFF381B20))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${lastMultiplier}x • +$lastWinAmount VCOIN",
                        color = if (isWin) NeonEmerald else Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pegboard Arena
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF0A0F1D))
                    )
                )
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width

                // Draw pegs
                for (r in 0 until rowCount) {
                    val count = r + 3
                    val startX = w * 0.5f - ((count - 1) * 22.dp.toPx()) / 2
                    val y = 25.dp.toPx() + r * 24.dp.toPx()
                    for (c in 0 until count) {
                        val x = startX + c * 22.dp.toPx()
                        drawCircle(Color.White.copy(alpha = 0.85f), radius = 3.dp.toPx(), center = Offset(x, y))
                    }
                }

                // Draw falling ball if active
                if (isDropping) {
                    val currX = w * ballX.value
                    val currY = 25.dp.toPx() + (ballY.value * (rowCount * 24.dp.toPx()))
                    drawCircle(Color(0xFFFF3366).copy(alpha = 0.35f), radius = 12.dp.toPx(), center = Offset(currX, currY))
                    drawCircle(Color(0xFFFF3366), radius = 7.dp.toPx(), center = Offset(currX, currY))
                    drawCircle(Color.White, radius = 2.5.dp.toPx(), center = Offset(currX - 2.dp.toPx(), currY - 2.dp.toPx()))
                }
            }

            // Bottom Multiplier Slots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                multipliers.forEachIndexed { idx, mult ->
                    val isHit = activeSlotIndex == idx
                    val slotColor = when {
                        mult >= 10.0 -> Color(0xFFFF3366)
                        mult >= 2.0 -> Color(0xFFFF9900)
                        mult >= 1.0 -> Color(0xFFFFD700)
                        else -> Color(0xFF334155)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isHit) PrimaryGold else slotColor)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${mult}x",
                            color = if (isHit) DarkBackground else Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Risk Level Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("LOW", "MEDIUM", "HIGH").forEach { level ->
                val isSelected = riskLevel == level
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PrimaryGold else DarkSurface)
                        .border(1.dp, if (isSelected) PrimaryGold else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isDropping) { riskLevel = level }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level,
                        color = if (isSelected) DarkBackground else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bet Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(10L, 50L, 100L, 500L).forEach { amount ->
                CasinoBetChip(
                    text = "$amount",
                    isSelected = betAmount == amount,
                    onClick = { if (!isDropping) betAmount = amount }
                )
            }
            CasinoBetChip(
                text = "MAX",
                isSelected = betAmount == userBalance.coerceAtMost(5000L),
                onClick = { if (!isDropping) betAmount = userBalance.coerceAtMost(5000L) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Drop Ball Action Button
        CasinoPrimaryButton(
            text = if (isDropping) "DROPPING BALL..." else "DROP BALL ($betAmount VCOIN)",
            enabled = !isDropping && userBalance >= betAmount,
            onClick = {
                if (userBalance < betAmount) return@CasinoPrimaryButton
                onBalanceChange(userBalance - betAmount)
                isDropping = true
                activeSlotIndex = null

                scope.launch {
                    ballY.snapTo(0f)
                    var xPos = 0.5f
                    val pathSteps = mutableListOf<Float>()

                    // Simulate Galton board left/right bounces
                    for (step in 1..rowCount) {
                        val goRight = Random.nextBoolean()
                        val stepOffset = 0.05f
                        xPos = if (goRight) (xPos + stepOffset) else (xPos - stepOffset)
                        xPos = xPos.coerceIn(0.1f, 0.9f)
                        pathSteps.add(xPos)
                    }

                    // Animate through rows
                    for (i in 0 until rowCount) {
                        ballX.animateTo(pathSteps[i], tween(120))
                        ballY.animateTo((i + 1).toFloat() / rowCount, tween(120))
                        delay(40)
                    }

                    // Map final X position to multiplier slot index (0..8)
                    val slotIdx = ((pathSteps.last() - 0.1f) / 0.8f * (multipliers.size - 1))
                        .toInt()
                        .coerceIn(0, multipliers.size - 1)

                    activeSlotIndex = slotIdx
                    val hitMult = multipliers[slotIdx]
                    val win = (betAmount * hitMult).toLong()

                    lastMultiplier = hitMult
                    lastWinAmount = win
                    onBalanceChange(userBalance - betAmount + win)

                    delay(400)
                    isDropping = false
                }
            }
        )
    }
}
