package com.example.exit_plan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = Slate40,
    onSecondary = Color.White,
    secondaryContainer = Slate90,
    tertiary = Amber40,
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    error = Red40,
    background = SurfaceLight,
    surface = SurfaceLight,
)

private val DarkColors = darkColorScheme(
    primary = Green80,
    onPrimary = Green10,
    primaryContainer = Green40,
    onPrimaryContainer = Green90,
    secondary = Slate80,
    secondaryContainer = Slate40,
    tertiary = Amber80,
    tertiaryContainer = Amber40,
    error = Red80,
    background = SurfaceDark,
    surface = SurfaceDark,
)

@Composable
fun AntitrapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
