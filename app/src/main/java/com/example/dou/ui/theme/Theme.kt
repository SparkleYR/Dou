package com.example.dou.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ══════════════════════════════════════════════════════════════════════
// AMOLED Dark Theme for Dou
// Optimized for OLED displays with true black backgrounds
// ══════════════════════════════════════════════════════════════════════

private val AmoledColorScheme = darkColorScheme(
    // Primary colors
    primary = AccentBlue,
    onPrimary = TrueBlack,
    primaryContainer = AccentBlue.copy(alpha = 0.2f),
    onPrimaryContainer = AccentBlue,
    
    // Secondary colors
    secondary = AccentCyan,
    onSecondary = TrueBlack,
    secondaryContainer = AccentCyan.copy(alpha = 0.2f),
    onSecondaryContainer = AccentCyan,
    
    // Tertiary colors
    tertiary = AccentPurple,
    onTertiary = TrueBlack,
    tertiaryContainer = AccentPurple.copy(alpha = 0.2f),
    onTertiaryContainer = AccentPurple,
    
    // Background & Surface - TRUE BLACK for AMOLED
    background = TrueBlack,
    onBackground = ClockWhite,
    surface = TrueBlack,
    onSurface = ClockWhite,
    surfaceVariant = SoftBlack,
    onSurfaceVariant = ClockDim,
    
    // Container colors
    surfaceContainerLowest = TrueBlack,
    surfaceContainerLow = SoftBlack,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DimGray,
    surfaceContainerHighest = SubtleGray,
    
    // Outline colors
    outline = DimGray,
    outlineVariant = SubtleGray,
    
    // Inverse colors
    inverseSurface = ClockWhite,
    inverseOnSurface = TrueBlack,
    inversePrimary = AccentBlue,
    
    // Error colors
    error = AccentRed,
    onError = TrueBlack,
    errorContainer = AccentRed.copy(alpha = 0.2f),
    onErrorContainer = AccentRed,
    
    // Scrim
    scrim = TrueBlack
)

@Composable
fun DouTheme(
    darkTheme: Boolean = true, // Always dark for dock mode
    content: @Composable () -> Unit
) {
    val colorScheme = AmoledColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use WindowInsetsController for API 35+
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
            // Set navigation bar color using the modern approach
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = TrueBlack.toArgb()
                window.navigationBarColor = TrueBlack.toArgb()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}