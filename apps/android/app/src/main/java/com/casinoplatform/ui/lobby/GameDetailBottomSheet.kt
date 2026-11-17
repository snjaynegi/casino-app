package com.casinoplatform.ui.lobby

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.data.model.GameItem
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.StatCard
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

@Composable
fun GameDetailDialog(
    game: GameItem,
    onLaunchGame: (GameItem) -> Unit,
    onDismiss: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Drag handle / Dismiss bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = game.category.uppercase(),
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isFavorite) "★ Favorited" else "☆ Add to Favs",
                        color = if (isFavorite) PrimaryGold else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onToggleFavorite(game.id) }
                            .padding(end = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }

            // Title & Description
            Text(
                text = game.title,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = game.description,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Target RTP",
                    value = game.rtp,
                    subtitle = "Fair Return",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Volatility",
                    value = game.volatility,
                    subtitle = "Risk Tier",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Max Multiplier",
                    value = game.maxMultiplier,
                    subtitle = "Payout Cap",
                    modifier = Modifier.weight(1f)
                )
            }

            // How to Play Rules
            Text(
                text = "HOW TO PLAY & GAME RULES",
                color = PrimaryGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                game.rules.forEachIndexed { idx, rule ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "${idx + 1}.",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = rule,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Betting Bounds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Virtual Bet Limits: ${game.minBet} - ${game.maxBet} VCOIN",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "Provably Fair 🛡️",
                    color = NeonEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Play CTA
            CasinoPrimaryButton(
                text = "PLAY ${game.title.uppercase()}",
                onClick = { onLaunchGame(game) }
            )
        }
    }
}
