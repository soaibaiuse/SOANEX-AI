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

private val SoanexDarkColorScheme = darkColorScheme(
    primary = CyanAccentMuted,
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = NeonPurple,
    onSecondary = Color(0xFF3B0764),
    secondaryContainer = Color(0xFF581C87),
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = DeepViolet,
    background = DarkCanvas,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF1E293B)
)

private val SoanexLightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF7C3AED),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF6D28D9),
    tertiary = Color(0xFF4F46E5),
    background = LightCanvas,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand identity by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SoanexDarkColorScheme
        else -> SoanexLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
