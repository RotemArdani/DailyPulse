package com.colman.dailypulse.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun DailyPulseTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        background = Background,
        surface = Surface,
        onPrimary = White,
        onSecondary = White,
        onBackground = White,
        onSurface = White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}