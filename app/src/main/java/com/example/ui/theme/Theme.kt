package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumDarkColorScheme = darkColorScheme(
  primary = YoutubeRed,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF3F0000),
  onPrimaryContainer = Color.White,
  secondary = TextSecondary,
  onSecondary = Color.Black,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = BorderColor,
  onSurfaceVariant = TextSecondary,
  outline = BorderColor
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // AMOLED Black premium theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = PremiumDarkColorScheme,
    typography = Typography,
    content = content
  )
}
