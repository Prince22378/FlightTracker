package com.example.flighttrackerques2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography


private val LightColors = lightColorScheme(
    primary = Color(0xFF1E88E5),
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

@Composable
fun FlightTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val Typography = Typography()
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
