package com.casinoplatform.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Background & Surfaces
val DarkBackground = Color(0xFF0B0E14)
val DarkSurface = Color(0xFF121824)
val DarkSurfaceVariant = Color(0xFF1A2234)
val DarkSurfaceElevated = Color(0xFF222B42)
val DarkSurfaceBorder = Color(0xFF2E3852)

// Premium Gold Accents
val PrimaryGold = Color(0xFFF59E0B)
val GoldHighlight = Color(0xFFFCD34D)
val GoldDark = Color(0xFFB45309)

// Gradients
val PurpleGradientStart = Color(0xFF8B5CF6)
val PurpleGradientEnd = Color(0xFF6D28D9)

val GoldButtonGradient = Brush.horizontalGradient(
    colors = listOf(PrimaryGold, GoldHighlight)
)

val PurpleButtonGradient = Brush.horizontalGradient(
    colors = listOf(PurpleGradientStart, PurpleGradientEnd)
)

val CardGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0x338B5CF6), Color(0x000B0E14))
)

// Neon Accent Colors
val NeonCyan = Color(0xFF06B6D4)
val NeonCyanGlow = Color(0x3306B6D4)
val NeonEmerald = Color(0xFF10B981)
val NeonEmeraldGlow = Color(0x3310B981)
val NeonCrimson = Color(0xFFEF4444)
val NeonCrimsonGlow = Color(0x33EF4444)

// Text Hierarchy
val TextPrimary = Color(0xFFF9FAFB)
val TextSecondary = Color(0xFF9CA3AF)
val TextMuted = Color(0xFF6B7280)
val TextGold = Color(0xFFFDE68A)
