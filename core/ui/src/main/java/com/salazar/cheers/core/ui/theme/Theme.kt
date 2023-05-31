package com.salazar.cheers.core.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    onBackground = Color.White,
    background = Color(0xFF101010),
    surface = Color(0xFF101010),
    onSurface = Color.White,
)

private val LightColorPalette = lightColorScheme(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    outline = Color(0xFFDBDBDB),
)

@Composable
fun CheersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}