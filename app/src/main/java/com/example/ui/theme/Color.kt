package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary IPTV Palette
val DarkNavyBg = Color(0xFF0B0F1A)
val DarkNavySurface = Color(0xFF0E1422)
val DarkNavyCard = Color(0xFF1B273F)
val DarkNavyCardHover = Color(0xFF243454)
val DarkNavySurfaceVariant = Color(0xFF141C2E)

// Gold Accents
val GoldPrimary = Color(0xFFE5A93C)
val GoldLight = Color(0xFFF6C860)
val GoldDark = Color(0xFFA67C1E)
val GoldGlow = Color(0x66E5A93C)

// Typography & Text
val TextPrimary = Color(0xFFF1F3F7)
val TextSecondary = Color(0xFF9AA6B8)
val TextTertiary = Color(0xFF64748B)

// Status & Indicators
val StatusSuccess = Color(0xFF10B981)
val StatusWarning = Color(0xFFF59E0B)
val StatusError = Color(0xFFEF4444)
val StatusLive = Color(0xFFE11D48)
val StatusInfo = Color(0xFF38BDF8)

// Borders & Glassmorphism
val GlassBorder = Color(0x26E5A93C)
val GlassBorderSubtle = Color(0x1AFFFFFF)
val GlassOverlay = Color(0x4D0E1422)
val GlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0x331B273F), Color(0x1A0E1422))
)
val GoldGradient = Brush.horizontalGradient(
    colors = listOf(GoldDark, GoldPrimary, GoldLight)
)
val HeroOverlayGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0x990B0F1A), DarkNavyBg)
)
