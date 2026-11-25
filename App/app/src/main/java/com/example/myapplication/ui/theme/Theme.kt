package com.atomiccryptids.campsiteapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = White,
    secondary = SageGreen,
    onSecondary = Charcoal,
    tertiary = WarmSand,
    onTertiary = Charcoal,
    background = Cream,
    onBackground = Charcoal,
    surface = White,
    onSurface = Charcoal,
    primaryContainer = LightSage,
    onPrimaryContainer = DarkForest,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = LightSage,
    onPrimary = DarkForest,
    secondary = SageGreen,
    onSecondary = DarkForest,
    tertiary = WarmSand,
    onTertiary = Charcoal,
    background = DarkForest,
    onBackground = Cream,
    surface = Charcoal,
    onSurface = Cream,
    primaryContainer = ForestGreen,
    onPrimaryContainer = LightSage,
    error = ErrorRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}