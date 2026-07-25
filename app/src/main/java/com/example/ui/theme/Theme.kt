package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KernelDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = MatrixGreen,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = MatrixGreen,
    tertiary = AmberWarning,
    onTertiary = Color.Black,
    background = DarkKernelBg,
    onBackground = TextPrimary,
    surface = DarkSurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun KernelMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce the sleek OLED Dark theme as requested for Kernel Monitor
    MaterialTheme(
        colorScheme = KernelDarkColorScheme,
        typography = Typography,
        content = content
    )
}

