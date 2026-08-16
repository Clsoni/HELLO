package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = SwastikGold,
    secondary = AntiqueLight,
    tertiary = BrandRed,
    background = AntiqueDark,
    surface = AntiqueDark,
    onPrimary = AntiqueDark,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AntiqueDark,
    secondary = AntiqueLight,
    tertiary = BrandRed,
    background = Color(0xFFF0FAFF),
    surface = Color.White,
    onPrimary = SwastikGold,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = AntiqueDark,
    onSurface = AntiqueDark
  )

@Composable
fun SwastikGoldTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
