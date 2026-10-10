package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BilimMarkaziColorScheme = darkColorScheme(
    primary = GoldHistory,
    onPrimary = DarkNavyBg,
    primaryContainer = GoldHistoryContainer,
    onPrimaryContainer = GoldHistoryLight,
    secondary = BlueGeography,
    onSecondary = DarkNavyBg,
    secondaryContainer = BlueGeographyContainer,
    onSecondaryContainer = BlueGeographyLight,
    tertiary = StatusPresent,
    onTertiary = DarkNavyBg,
    background = DarkNavyBg,
    onBackground = TextWhite,
    surface = DarkNavySurface,
    onSurface = TextWhite,
    surfaceVariant = DarkNavySurfaceVariant,
    onSurfaceVariant = TextMutedGray,
    outline = DarkNavyCardBorder,
    outlineVariant = DividerColor,
    error = StatusAbsent,
    onError = TextWhite,
    errorContainer = StatusAbsentContainer,
    onErrorContainer = StatusAbsent
)

@Composable
fun BilimMarkaziTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BilimMarkaziColorScheme,
        typography = Typography,
        content = content
    )
}
