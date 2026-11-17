package com.casinoplatform.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.GoldDark
import com.casinoplatform.ui.theme.NeonCrimson
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

@Composable
fun WalletBalanceBadge(
    balance: Long,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, GoldDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Gold coin emblem
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(PrimaryGold),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    color = DarkBackground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            AnimatedContent(
                targetState = balance,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
                    } else {
                        slideInVertically { height -> -height } togetherWith slideOutVertically { height -> height }
                    }
                },
                label = "balance_anim"
            ) { targetBalance ->
                Text(
                    text = String.format("%,d", targetBalance),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "VCOIN",
                color = PrimaryGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = PrimaryGold,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
fun EmptyStateView(
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎰",
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        if (actionText != null) {
            Spacer(modifier = Modifier.height(20.dp))
            CasinoSecondaryButton(
                text = actionText,
                onClick = onActionClick,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
fun ErrorBanner(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NeonCrimson.copy(alpha = 0.12f))
            .border(1.dp, NeonCrimson.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = TextPrimary,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RETRY",
                    color = NeonCrimson,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onRetry)
                )
            }
        }
    }
}
