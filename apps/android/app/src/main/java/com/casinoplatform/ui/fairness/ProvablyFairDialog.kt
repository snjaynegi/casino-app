package com.casinoplatform.ui.fairness

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.components.CasinoSecondaryButton
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

@Composable
fun ProvablyFairDialog(
    roundId: String,
    multiplier: Double,
    serverSeed: String,
    serverSeedHash: String,
    clientSeed: String,
    nonce: Long,
    isVerified: Boolean = true,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PROVABLY FAIR AUDIT",
                        color = PrimaryGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonEmerald.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isVerified) "100% VERIFIED" else "UNVERIFIED",
                            color = NeonEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

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

            Text(
                text = "Round #$roundId • Outcome: ${String.format("%.2fx", multiplier)}",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Parameter Cards
            AuditField(label = "Server Seed (Plaintext Revealed)", value = serverSeed)
            AuditField(label = "Server Seed SHA-256 Hash (Committed Pre-Round)", value = serverSeedHash)
            AuditField(label = "Client Seed", value = clientSeed)
            AuditField(label = "Round Nonce", value = nonce.toString())

            Text(
                text = "Outcome is derived as: HMAC-SHA512(ServerSeed, ClientSeed + \":\" + Nonce). Neither the casino nor the player can manipulate the result after commitment.",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            CasinoSecondaryButton(
                text = "CLOSE AUDIT",
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun AuditField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkBackground)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 2
            )
        }
    }
}
