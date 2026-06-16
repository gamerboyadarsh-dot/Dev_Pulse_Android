package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
)

// Default app theme to light
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // We will force light theme for the "High Density" aesthetic
    dynamicColor: Boolean = false, // Disabled dynamic color to strictly enforce our branded mode
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
