package com.casinoplatform.ui.profile

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.components.CasinoSecondaryButton
import com.casinoplatform.ui.components.WalletBalanceBadge
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.NeonCrimson
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    username: String = "NeonPilot",
    email: String = "pilot@casino.test",
    level: Int = 12,
    balance: Long = 10000L,
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTimeLimit by remember { mutableStateOf("60m") }
    var coolOffActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "PLAYER PROFILE",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold
        )

        // Player Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .border(2.dp, PrimaryGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = username.take(1).uppercase(),
                        color = PrimaryGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = username,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "VIP Level $level",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Wallet Balance Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "VIRTUAL WALLET BALANCE",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                WalletBalanceBadge(balance = balance)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Virtual entertainment currency only. Non-redeemable.",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }
        }

        // Responsible Gaming Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "RESPONSIBLE GAMING CONTROLS",
                    fontSize = 14.sp,
                    color = PrimaryGold,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Daily Session Time Limit",
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("30m", "60m", "120m", "No Limit").forEach { limit ->
                        val isSelected = selectedTimeLimit == limit
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryGold else DarkSurfaceElevated)
                                .clickable { selectedTimeLimit = limit }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = limit,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Self-Exclusion / Cool-Off",
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (coolOffActive) NeonEmerald.copy(alpha = 0.2f) else DarkSurfaceElevated)
                        .clickable { coolOffActive = !coolOffActive }
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (coolOffActive) "✓ 24-Hour Cool-Off Active" else "Activate 24-Hour Cool-Off Period",
                        color = if (coolOffActive) NeonEmerald else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Account Actions
        CasinoSecondaryButton(
            text = "SIGN OUT",
            onClick = onSignOut
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDeleteAccount)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Delete Account (Right to be Forgotten)",
                color = NeonCrimson,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
