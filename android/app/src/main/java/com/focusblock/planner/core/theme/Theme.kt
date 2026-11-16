package com.focusblock.planner.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceAlt,
    onPrimaryContainer = TextPrimary,
    secondary = SurfaceAlt,
    onSecondary = TextPrimary,
    tertiary = Accent,
    onTertiary = OnAccent,
    background = BgDeep,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = BgRaised,
    onSurfaceVariant = TextSecondary,
    outline = DividerSoft,
    outlineVariant = BorderSoft,
    error = Accent,
    onError = OnAccent,
    scrim = BgSink
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
