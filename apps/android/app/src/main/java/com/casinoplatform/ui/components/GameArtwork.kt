package com.casinoplatform.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold

@Composable
fun GameArtwork(
    gameId: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        when (gameId) {
            "rocket-rescue" -> RocketRescueArtwork()
            "mines-grid" -> MinesGridArtwork()
            "cyber-dice" -> CyberDiceArtwork()
            "plinko-physics" -> PlinkoArtwork()
            "frog-frenzy" -> FrogFrenzyArtwork()
            "deep-dive" -> DeepDiveArtwork()
            "sky-elevator" -> SkyElevatorArtwork()
            "neon-bounce" -> NeonBounceArtwork()
            else -> GenericCasinoArtwork(gameId)
        }
    }
}

@Composable
private fun RocketRescueArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0628), Color(0xFF1E0A4A), Color(0xFF380835))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background stars
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 2.dp.toPx(), center = Offset(w * 0.15f, h * 0.25f))
            drawCircle(Color.White.copy(alpha = 0.6f), radius = 1.5.dp.toPx(), center = Offset(w * 0.45f, h * 0.15f))
            drawCircle(PrimaryGold.copy(alpha = 0.9f), radius = 2.5.dp.toPx(), center = Offset(w * 0.85f, h * 0.35f))
            drawCircle(Color.White.copy(alpha = 0.5f), radius = 1.8.dp.toPx(), center = Offset(w * 0.3f, h * 0.7f))

            // Ascending parabolic trajectory curve
            val traj = Path().apply {
                moveTo(w * 0.05f, h * 0.9f)
                cubicTo(w * 0.35f, h * 0.85f, w * 0.55f, h * 0.65f, w * 0.78f, h * 0.28f)
            }
            drawPath(traj, color = NeonCyan.copy(alpha = 0.7f), style = Stroke(width = 3.dp.toPx()))

            // Rocket exhaust glow & flames
            val flamePath = Path().apply {
                moveTo(w * 0.76f, h * 0.30f)
                lineTo(w * 0.68f, h * 0.40f)
                lineTo(w * 0.73f, h * 0.33f)
                close()
            }
            drawPath(flamePath, color = Color(0xFFFF5722))

            // Rocket fuselage
            val rocketPath = Path().apply {
                moveTo(w * 0.85f, h * 0.18f) // Nose cone
                lineTo(w * 0.75f, h * 0.33f)
                lineTo(w * 0.78f, h * 0.26f)
                close()
            }
            drawPath(rocketPath, color = PrimaryGold)

            // Rocket fin
            drawCircle(Color(0xFF00F5A0), radius = 3.dp.toPx(), center = Offset(w * 0.80f, h * 0.24f))
        }

        // Live Multiplier Tag
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("10,000x", color = NeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MinesGridArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0A1215), Color(0xFF102A24), Color(0xFF091F18))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 5x5 grid preview background tiles
            val tileSize = w * 0.12f
            for (row in 0..2) {
                for (col in 0..4) {
                    val x = w * 0.12f + col * (tileSize + 4.dp.toPx())
                    val y = h * 0.15f + row * (tileSize + 4.dp.toPx())
                    drawRoundRect(
                        color = Color(0xFF1B3830),
                        topLeft = Offset(x, y),
                        size = Size(tileSize, tileSize)
                    )
                }
            }

            // Central Sparkling Diamond
            val diamond = Path().apply {
                moveTo(w * 0.35f, h * 0.35f)
                lineTo(w * 0.45f, h * 0.22f)
                lineTo(w * 0.55f, h * 0.22f)
                lineTo(w * 0.65f, h * 0.35f)
                lineTo(w * 0.50f, h * 0.55f)
                close()
            }
            drawPath(diamond, color = Color(0xFF00F5A0))

            // Diamond facets
            drawLine(Color.White.copy(alpha = 0.8f), Offset(w * 0.45f, h * 0.22f), Offset(w * 0.50f, h * 0.55f), strokeWidth = 1.5.dp.toPx())
            drawLine(Color.White.copy(alpha = 0.8f), Offset(w * 0.55f, h * 0.22f), Offset(w * 0.50f, h * 0.55f), strokeWidth = 1.5.dp.toPx())

            // Bomb on the side
            val bombCenter = Offset(w * 0.80f, h * 0.65f)
            drawCircle(Color(0xFF12141A), radius = 16.dp.toPx(), center = bombCenter)
            drawCircle(Color(0xFFFF3366), radius = 4.dp.toPx(), center = Offset(bombCenter.x + 12.dp.toPx(), bombCenter.y - 14.dp.toPx()))
            drawLine(Color(0xFFFFB800), Offset(bombCenter.x + 8.dp.toPx(), bombCenter.y - 10.dp.toPx()), Offset(bombCenter.x + 14.dp.toPx(), bombCenter.y - 16.dp.toPx()), strokeWidth = 2.dp.toPx())
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("5x5 GEM RUSH", color = Color(0xFF00F5A0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CyberDiceArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1C0A35), Color(0xFF2D1254), Color(0xFF140728))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Dice 1 (Primary 3D isometric representation)
            val d1X = w * 0.35f
            val d1Y = h * 0.45f
            val s = 22.dp.toPx()

            // Front face
            drawRoundRect(
                color = Color(0xFF7928CA),
                topLeft = Offset(d1X - s, d1Y - s),
                size = Size(s * 2, s * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            // Glowing border
            drawRoundRect(
                color = Color(0xFF00F5A0),
                topLeft = Offset(d1X - s, d1Y - s),
                size = Size(s * 2, s * 2),
                style = Stroke(width = 2.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            // Pips on Front face (5 dots)
            val pipColor = PrimaryGold
            val pipR = 2.5.dp.toPx()
            drawCircle(pipColor, pipR, Offset(d1X, d1Y))
            drawCircle(pipColor, pipR, Offset(d1X - s * 0.5f, d1Y - s * 0.5f))
            drawCircle(pipColor, pipR, Offset(d1X + s * 0.5f, d1Y - s * 0.5f))
            drawCircle(pipColor, pipR, Offset(d1X - s * 0.5f, d1Y + s * 0.5f))
            drawCircle(pipColor, pipR, Offset(d1X + s * 0.5f, d1Y + s * 0.5f))

            // Dice 2 (Secondary angled dice behind)
            val d2X = w * 0.70f
            val d2Y = h * 0.38f
            val s2 = 18.dp.toPx()
            drawRoundRect(
                color = Color(0xFF3B1569),
                topLeft = Offset(d2X - s2, d2Y - s2),
                size = Size(s2 * 2, s2 * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
            drawRoundRect(
                color = PrimaryGold,
                topLeft = Offset(d2X - s2, d2Y - s2),
                size = Size(s2 * 2, s2 * 2),
                style = Stroke(width = 1.5.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
            drawCircle(pipColor, pipR, Offset(d2X - s2 * 0.4f, d2Y - s2 * 0.4f))
            drawCircle(pipColor, pipR, Offset(d2X + s2 * 0.4f, d2Y + s2 * 0.4f))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("ROLL OVER 50", color = PrimaryGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PlinkoArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B1120), Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Pegs pyramid
            val rows = 5
            for (r in 0 until rows) {
                val count = r + 3
                val startX = w * 0.5f - ((count - 1) * 14.dp.toPx()) / 2
                val y = h * 0.2f + r * 14.dp.toPx()
                for (c in 0 until count) {
                    val x = startX + c * 14.dp.toPx()
                    drawCircle(Color.White.copy(alpha = 0.9f), radius = 2.dp.toPx(), center = Offset(x, y))
                }
            }

            // Falling glowing ball
            val ballCenter = Offset(w * 0.55f, h * 0.45f)
            drawCircle(Color(0xFFFF3366).copy(alpha = 0.4f), radius = 10.dp.toPx(), center = ballCenter)
            drawCircle(Color(0xFFFF3366), radius = 6.dp.toPx(), center = ballCenter)
            drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(ballCenter.x - 2.dp.toPx(), ballCenter.y - 2.dp.toPx()))

            // Bottom multiplier bins
            val binCount = 7
            val binW = w / (binCount + 1)
            val colors = listOf(Color(0xFFFF3366), Color(0xFFFF9900), Color(0xFFFFD700), Color(0xFF00F5A0), Color(0xFFFFD700), Color(0xFFFF9900), Color(0xFFFF3366))
            for (i in 0 until binCount) {
                val bx = (i + 0.5f) * binW
                drawRoundRect(
                    color = colors[i],
                    topLeft = Offset(bx, h * 0.82f),
                    size = Size(binW * 0.85f, 10.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("1000x PEG DROP", color = Color(0xFFFF9900), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FrogFrenzyArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF041C15), Color(0xFF0B3328), Color(0xFF07241C))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Water ripple circles
            drawCircle(Color(0xFF00F5A0).copy(alpha = 0.15f), radius = 30.dp.toPx(), center = Offset(w * 0.35f, h * 0.65f), style = Stroke(width = 1.dp.toPx()))
            drawCircle(Color(0xFF00F5A0).copy(alpha = 0.25f), radius = 18.dp.toPx(), center = Offset(w * 0.35f, h * 0.65f), style = Stroke(width = 1.5.dp.toPx()))

            // Glowing Lily pads
            drawOval(Color(0xFF00A86B), topLeft = Offset(w * 0.15f, h * 0.60f), size = Size(w * 0.35f, h * 0.22f))
            drawOval(Color(0xFF00F5A0), topLeft = Offset(w * 0.15f, h * 0.60f), size = Size(w * 0.35f, h * 0.22f), style = Stroke(width = 2.dp.toPx()))

            drawOval(Color(0xFF008F5B), topLeft = Offset(w * 0.60f, h * 0.30f), size = Size(w * 0.32f, h * 0.20f))
            drawOval(Color(0xFF00F5A0).copy(alpha = 0.8f), topLeft = Offset(w * 0.60f, h * 0.30f), size = Size(w * 0.32f, h * 0.20f), style = Stroke(width = 1.5.dp.toPx()))

            // Neon Cyber Frog leaping forward
            val frogHead = Offset(w * 0.48f, h * 0.42f)
            drawCircle(Color(0xFF00F5A0), radius = 14.dp.toPx(), center = frogHead)
            // Big frog eyes
            drawCircle(PrimaryGold, radius = 5.dp.toPx(), center = Offset(frogHead.x - 7.dp.toPx(), frogHead.y - 10.dp.toPx()))
            drawCircle(PrimaryGold, radius = 5.dp.toPx(), center = Offset(frogHead.x + 7.dp.toPx(), frogHead.y - 10.dp.toPx()))
            drawCircle(Color.Black, radius = 2.5.dp.toPx(), center = Offset(frogHead.x - 7.dp.toPx(), frogHead.y - 10.dp.toPx()))
            drawCircle(Color.Black, radius = 2.5.dp.toPx(), center = Offset(frogHead.x + 7.dp.toPx(), frogHead.y - 10.dp.toPx()))

            // Leap trajectory arc
            val jumpArc = Path().apply {
                moveTo(w * 0.30f, h * 0.65f)
                quadraticBezierTo(w * 0.45f, h * 0.30f, w * 0.72f, h * 0.38f)
            }
            drawPath(jumpArc, color = PrimaryGold.copy(alpha = 0.7f), style = Stroke(width = 2.dp.toPx()))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("HOP & SURVIVE", color = Color(0xFF00F5A0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DeepDiveArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF021B2D), Color(0xFF01111E), Color(0xFF00080F))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Light rays from surface
            val ray = Path().apply {
                moveTo(w * 0.2f, 0f)
                lineTo(w * 0.4f, 0f)
                lineTo(w * 0.65f, h)
                lineTo(w * 0.35f, h)
                close()
            }
            drawPath(ray, color = Color(0xFF00E5FF).copy(alpha = 0.12f))

            // Submarine hull
            val subCenter = Offset(w * 0.45f, h * 0.45f)
            drawOval(
                color = PrimaryGold,
                topLeft = Offset(subCenter.x - 30.dp.toPx(), subCenter.y - 15.dp.toPx()),
                size = Size(60.dp.toPx(), 30.dp.toPx())
            )
            // Cockpit glass
            drawCircle(Color(0xFF00E5FF), radius = 7.dp.toPx(), center = Offset(subCenter.x + 15.dp.toPx(), subCenter.y))

            // Submarine periscope & propeller
            drawLine(PrimaryGold, Offset(subCenter.x, subCenter.y - 15.dp.toPx()), Offset(subCenter.x, subCenter.y - 24.dp.toPx()), strokeWidth = 3.dp.toPx())
            drawLine(Color(0xFFFF5722), Offset(subCenter.x - 30.dp.toPx(), subCenter.y - 8.dp.toPx()), Offset(subCenter.x - 30.dp.toPx(), subCenter.y + 8.dp.toPx()), strokeWidth = 4.dp.toPx())

            // Spotlight beam
            val beam = Path().apply {
                moveTo(subCenter.x + 25.dp.toPx(), subCenter.y)
                lineTo(w * 0.95f, subCenter.y - 20.dp.toPx())
                lineTo(w * 0.95f, subCenter.y + 20.dp.toPx())
                close()
            }
            drawPath(beam, color = Color(0xFFFFF176).copy(alpha = 0.25f))

            // Treasure chest in the deep
            val chestX = w * 0.78f
            val chestY = h * 0.68f
            drawRoundRect(Color(0xFFB78103), topLeft = Offset(chestX, chestY), size = Size(24.dp.toPx(), 16.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx()))
            drawCircle(PrimaryGold, radius = 3.dp.toPx(), center = Offset(chestX + 12.dp.toPx(), chestY + 6.dp.toPx()))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("ABYSS TREASURE", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SkyElevatorArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF130924), Color(0xFF261047), Color(0xFF0D0517))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // High-rise glass tower shaft
            val shaftX = w * 0.40f
            val shaftW = w * 0.20f
            drawRect(Color(0xFF1F1838), topLeft = Offset(shaftX, 0f), size = Size(shaftW, h))
            drawLine(NeonCyan.copy(alpha = 0.6f), Offset(shaftX, 0f), Offset(shaftX, h), strokeWidth = 2.dp.toPx())
            drawLine(NeonCyan.copy(alpha = 0.6f), Offset(shaftX + shaftW, 0f), Offset(shaftX + shaftW, h), strokeWidth = 2.dp.toPx())

            // Floor levels lines
            for (f in 1..4) {
                val fy = h * 0.2f * f
                drawLine(Color.White.copy(alpha = 0.3f), Offset(shaftX, fy), Offset(shaftX + shaftW, fy), strokeWidth = 1.dp.toPx())
            }

            // Glowing glass elevator car
            val carY = h * 0.35f
            drawRoundRect(
                color = PrimaryGold,
                topLeft = Offset(shaftX + 3.dp.toPx(), carY),
                size = Size(shaftW - 6.dp.toPx(), 28.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.9f),
                topLeft = Offset(shaftX + 6.dp.toPx(), carY + 4.dp.toPx()),
                size = Size(shaftW - 12.dp.toPx(), 12.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("TOWER CLIMB", color = PrimaryGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NeonBounceArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1F092B), Color(0xFF3B0B54), Color(0xFF14031E))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Bumper 1
            drawCircle(Color(0xFFFF3366), radius = 16.dp.toPx(), center = Offset(w * 0.3f, h * 0.35f))
            drawCircle(Color.White, radius = 6.dp.toPx(), center = Offset(w * 0.3f, h * 0.35f))

            // Bumper 2
            drawCircle(Color(0xFF00F5A0), radius = 14.dp.toPx(), center = Offset(w * 0.7f, h * 0.45f))
            drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(w * 0.7f, h * 0.45f))

            // High velocity neon ball & bounce shockwave
            val ball = Offset(w * 0.48f, h * 0.65f)
            drawCircle(Color(0xFFFFD700).copy(alpha = 0.3f), radius = 18.dp.toPx(), center = ball)
            drawCircle(Color(0xFFFFD700), radius = 8.dp.toPx(), center = ball)

            // Dynamic bounce vectors
            drawLine(Color(0xFFFFD700).copy(alpha = 0.8f), Offset(w * 0.3f, h * 0.35f), ball, strokeWidth = 2.dp.toPx())
            drawLine(Color(0xFF00F5A0).copy(alpha = 0.8f), ball, Offset(w * 0.7f, h * 0.45f), strokeWidth = 2.dp.toPx())
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkBackground.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("ARCADE PINBALL", color = Color(0xFFFF3366), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GenericCasinoArtwork(gameId: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = gameId.take(3).uppercase(),
            color = PrimaryGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
    }
}
