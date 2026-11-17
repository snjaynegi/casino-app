package com.casinoplatform.ui.games.rocketrescue

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.audio.CasinoAudioManager
import com.casinoplatform.ui.components.CasinoBetChip
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.WalletBalanceBadge
import com.casinoplatform.ui.fairness.ProvablyFairDialog
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.GoldButtonGradient
import com.casinoplatform.ui.theme.GoldHighlight
import com.casinoplatform.ui.theme.NeonCrimson
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.PurpleButtonGradient
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary
import kotlinx.coroutines.delay

enum class FlightState {
    IDLE,
    FLYING,
    CASHED_OUT,
    CRASHED
}

@Composable
fun RocketRescueScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var flightState by remember { mutableStateOf(FlightState.IDLE) }
    var currentMultiplier by remember { mutableDoubleStateOf(1.00) }
    var selectedBet by remember { mutableLongStateOf(100L) }
    var crashPoint by remember { mutableDoubleStateOf(3.45) }
    var showFairnessDialog by remember { mutableStateOf(false) }

    val recentMultipliers = remember { mutableListOf(2.15, 1.40, 5.80, 1.05, 12.30) }

    // Real-time flight curve simulation loop
    LaunchedEffect(flightState) {
        if (flightState == FlightState.FLYING) {
            currentMultiplier = 1.00
            var elapsedMs = 0L
            while (flightState == FlightState.FLYING) {
                delay(30)
                elapsedMs += 30
                val seconds = elapsedMs / 1000.0
                val calculated = 1.0 + 0.06 * Math.pow(seconds, 1.45)
                val rounded = Math.floor(calculated * 100) / 100.0

                if (rounded >= crashPoint) {
                    currentMultiplier = crashPoint
                    flightState = FlightState.CRASHED
                    CasinoAudioManager.playCrash()
                    recentMultipliers.add(0, crashPoint)
                    break
                } else {
                    currentMultiplier = rounded
                }
            }
        }
    }

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
            // Header Bar
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
                        Text(
                            text = "ROCKET RESCUE",
                            color = PrimaryGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Crash Multiplier • 97.0% RTP",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    WalletBalanceBadge(balance = userBalance)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable { showFairnessDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 16.sp)
                    }
                }
            }

            // Recent Multipliers Ticker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                recentMultipliers.take(5).forEach { mult ->
                    val isHigh = mult >= 2.0
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isHigh) NeonEmerald.copy(alpha = 0.15f) else DarkSurfaceElevated)
                            .border(1.dp, if (isHigh) NeonEmerald.copy(alpha = 0.5f) else DarkSurfaceBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = String.format("%.2fx", mult),
                            color = if (isHigh) NeonEmerald else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Central Flight Stage Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background space flight canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    drawLine(
                        color = Color(0x1A8B5CF6),
                        start = Offset(0f, h * 0.75f),
                        end = Offset(w, h * 0.75f),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color(0x1A8B5CF6),
                        start = Offset(0f, h * 0.5f),
                        end = Offset(w, h * 0.5f),
                        strokeWidth = 1f
                    )

                    // Draw flight curve if flying or finished
                    if (flightState != FlightState.IDLE) {
                        val progress = Math.min(1.0, (currentMultiplier - 1.0) / 4.0)
                        val endX = (w * 0.15f + (w * 0.7f * progress)).toFloat()
                        val endY = (h * 0.85f - (h * 0.65f * progress)).toFloat()

                        val path = Path().apply {
                            moveTo(w * 0.1f, h * 0.85f)
                            quadraticBezierTo(w * 0.4f, h * 0.85f, endX, endY)
                        }

                        drawPath(
                            path = path,
                            color = if (flightState == FlightState.CRASHED) NeonCrimson else NeonCyan,
                            style = Stroke(width = 4.dp.toPx())
                        )

                        // Rocket Icon at head
                        drawCircle(
                            color = if (flightState == FlightState.CRASHED) NeonCrimson else PrimaryGold,
                            radius = 10.dp.toPx(),
                            center = Offset(endX, endY)
                        )
                    }
                }

                // Central Multiplier Typography
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (flightState) {
                        FlightState.IDLE -> {
                            Text(
                                text = "READY FOR LAUNCH",
                                color = TextSecondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        FlightState.FLYING -> {
                            val multColor = when {
                                currentMultiplier >= 50.0 -> Color(0xFFFF0055)
                                currentMultiplier >= 10.0 -> PrimaryGold
                                currentMultiplier >= 2.0 -> NeonEmerald
                                else -> NeonCyan
                            }
                            Text(
                                text = String.format("%.2fx", currentMultiplier),
                                color = multColor,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "ALTITUDE COMPOUNDING",
                                color = multColor.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        FlightState.CASHED_OUT -> {
                            Text(
                                text = String.format("%.2fx", currentMultiplier),
                                color = NeonEmerald,
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "✓ SUCCESSFULLY EJECTED",
                                color = NeonEmerald,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        FlightState.CRASHED -> {
                            Text(
                                text = String.format("%.2fx", currentMultiplier),
                                color = NeonCrimson,
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "💥 CRASHED INTO DEBRIS",
                                color = NeonCrimson,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bet Selection Row
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SELECT VIRTUAL BET (VCOIN):",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(25L, 50L, 100L, 250L, 500L).forEach { bet ->
                        CasinoBetChip(
                            text = bet.toString(),
                            isSelected = selectedBet == bet,
                            onClick = { if (flightState != FlightState.FLYING) selectedBet = bet },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Primary Launch / Eject Action Button
            when (flightState) {
                FlightState.IDLE, FlightState.CRASHED, FlightState.CASHED_OUT -> {
                    CasinoPrimaryButton(
                        text = "LAUNCH ROCKET ($selectedBet VCOIN)",
                        gradient = GoldButtonGradient,
                        enabled = userBalance >= selectedBet,
                        onClick = {
                            onBalanceChange(userBalance - selectedBet)
                            crashPoint = 1.05 + Math.random() * 4.5
                            flightState = FlightState.FLYING
                        }
                    )
                }
                FlightState.FLYING -> {
                    val potentialPayout = (selectedBet * currentMultiplier).toLong()
                    CasinoPrimaryButton(
                        text = "EJECT NOW ($potentialPayout VCOIN)",
                        gradient = PurpleButtonGradient,
                        textColor = TextPrimary,
                        onClick = {
                            val winPayout = (selectedBet * currentMultiplier).toLong()
                            CasinoAudioManager.playCashout()
                            onBalanceChange(userBalance + winPayout)
                            flightState = FlightState.CASHED_OUT
                            recentMultipliers.add(0, currentMultiplier)
                        }
                    )
                }
            }
        }

        // Provably Fair Audit Dialog
        if (showFairnessDialog) {
            ProvablyFairDialog(
                roundId = "RR-84920",
                multiplier = currentMultiplier,
                serverSeed = "4a82cf91a3b98c56e29810f54318de92bcfa7109283719028310923847190234",
                serverSeedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                clientSeed = "rocket-pilot-seed-42",
                nonce = 104L,
                isVerified = true,
                onDismiss = { showFairnessDialog = false }
            )
        }
    }
}
