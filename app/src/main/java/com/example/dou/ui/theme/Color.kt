package com.example.dou.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════════════
// AMOLED-Optimized Color Palette for Dou
// True black (#000000) ensures OLED pixels are completely OFF = zero power
// ══════════════════════════════════════════════════════════════════════

// Background Colors - AMOLED Optimized
val TrueBlack = Color(0xFF000000)       // Primary background - pixels OFF
val SoftBlack = Color(0xFF0A0A0A)       // Subtle depth for cards
val DarkSurface = Color(0xFF121212)     // Elevated surfaces

// Clock Text Colors - High Contrast
val ClockWhite = Color(0xFFFFFFFF)      // Primary clock color
val ClockWarm = Color(0xFFFFF4E6)       // Warm white variant
val ClockCool = Color(0xFFE6F4FF)       // Cool white variant
val ClockDim = Color(0xFFB0B0B0)        // Dimmed/secondary text

// iOS-Inspired Accent Colors
val AccentBlue = Color(0xFF0A84FF)      // iOS blue - default accent
val AccentGreen = Color(0xFF30D158)     // iOS green - success/charging
val AccentOrange = Color(0xFFFF9F0A)    // iOS orange - warnings
val AccentPurple = Color(0xFFBF5AF2)    // iOS purple - accent option
val AccentPink = Color(0xFFFF375F)      // iOS pink - accent option
val AccentCyan = Color(0xFF64D2FF)      // iOS cyan - accent option
val AccentYellow = Color(0xFFFFD60A)    // iOS yellow - alerts
val AccentRed = Color(0xFFFF3B30)       // iOS red - errors/low battery

// Status Colors
val ChargingGreen = Color(0xFF32CD32)   // Charging indicator
val BatteryLow = Color(0xFFFF3B30)      // Low battery warning (<20%)
val BatteryMedium = Color(0xFFFFD60A)   // Medium battery (20-50%)
val BatteryGood = Color(0xFF30D158)     // Good battery (>50%)

// Subtle UI Elements
val DimGray = Color(0xFF2C2C2E)         // Borders, dividers
val SubtleGray = Color(0xFF48484A)      // Secondary elements
val MutedGray = Color(0xFF8E8E93)       // Placeholder text

// Music Widget Colors
val ProgressTrack = Color(0xFF3A3A3C)   // Progress bar background
val ProgressActive = Color(0xFFFFFFFF)  // Progress bar fill