package com.bold.trading.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BoldColors.PrimaryBullish,
    onPrimary = BoldColors.BgApp,
    primaryContainer = BoldColors.SurfaceElevated,
    secondary = BoldColors.PrimaryAccent,
    background = BoldColors.BgApp,
    surface = BoldColors.SurfaceCard,
    onSurface = BoldColors.TextPrimary,
    error = BoldColors.AccentBearish
)

@Composable
fun BoldTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = BoldTypography,
        content = content
    )
}
