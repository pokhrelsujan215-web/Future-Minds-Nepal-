package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkNavyBlue,
    secondary = DarkSkyBlue,
    tertiary = DarkCrimsonRed,
    background = AppDarkBg,
    surface = AppDarkSurface,
    onPrimary = Color(0xFF002253),
    onSecondary = Color(0xFF00293F),
    onTertiary = Color(0xFF4C000B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NavyBlue,
    secondary = SkyBlue,
    tertiary = CrimsonRed,
    background = AppLightBg,
    surface = AppLightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color to enforce our beautiful Nepali theme guidelines
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
