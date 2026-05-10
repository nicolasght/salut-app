package com.nicolasght.salut

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4FC3F7),
    onPrimary = Color(0xFF00131C),
    background = Color(0xFF0F1115),
    onBackground = Color(0xFFE6E8EB),
    surface = Color(0xFF161A22),
    onSurface = Color(0xFFE6E8EB),
    surfaceVariant = Color(0xFF1E232D),
    onSurfaceVariant = Color(0xFFB0B6BE),
)

@Composable
fun SalutTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}

object SalutColors {
    val Up = Color(0xFF4CAF50)
    val Down = Color(0xFFE53935)
    val Muted = Color(0xFF8A92A0)
}
