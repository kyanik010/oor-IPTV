package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkIptvColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = DarkNavyBg,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = DarkNavyBg,
    secondaryContainer = DarkNavyCard,
    onSecondaryContainer = TextPrimary,
    background = DarkNavyBg,
    onBackground = TextPrimary,
    surface = DarkNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle,
    error = StatusError,
    onError = TextPrimary
)

@Composable
fun NoorIptvTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkNavyBg.toArgb()
                window.navigationBarColor = DarkNavyBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkIptvColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
