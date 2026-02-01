package com.example.dou.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ══════════════════════════════════════════════════════════════════════
// Typography System for Dou - Optimized for Dock Display
// Large, readable text that's visible from a distance
// ══════════════════════════════════════════════════════════════════════

// Clock Typography - Extra large for primary time display
// Using Light weight consistently for elegance without being too thin
val ClockDisplayLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 100.sp,
    lineHeight = 108.sp,
    letterSpacing = (-2).sp
)

val ClockDisplayMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 80.sp,
    lineHeight = 88.sp,
    letterSpacing = (-1.5).sp
)

val ClockDisplaySmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 64.sp,
    lineHeight = 72.sp,
    letterSpacing = (-1).sp
)

// Date Typography - Light weight to match clock
val DateDisplayLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 22.sp,
    lineHeight = 30.sp,
    letterSpacing = 1.sp
)

val DateDisplayMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 18.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.8.sp
)

// Widget Typography - Light weight to match clock aesthetic
val WidgetTitle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.sp
)

val WidgetBody = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp
)

val WidgetCaption = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

// Temperature Display - Light weight for consistency
val TemperatureLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 40.sp,
    lineHeight = 48.sp,
    letterSpacing = (-0.5).sp
)

val TemperatureMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.sp
)

// Status Typography (Battery, Alarm) - Light weight
val StatusText = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 15.sp,
    lineHeight = 21.sp,
    letterSpacing = 0.15.sp
)

val StatusTextSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.25.sp
)

// Music Widget Typography - Hierarchy for balance
val TrackTitle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.sp
)

// Bold title for visual weight against album art
val TrackTitleBold = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.sp
)

val TrackArtist = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp
)

// Extra light artist for hierarchy contrast
val TrackArtistLight = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.ExtraLight,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.3.sp
)

val TrackDuration = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

// Material3 Typography - Mapped for theme compatibility
val Typography = Typography(
    displayLarge = ClockDisplayLarge,
    displayMedium = ClockDisplayMedium,
    displaySmall = ClockDisplaySmall,
    headlineLarge = DateDisplayLarge,
    headlineMedium = DateDisplayMedium,
    headlineSmall = WidgetTitle,
    titleLarge = TrackTitle,
    titleMedium = StatusText,
    titleSmall = StatusTextSmall,
    bodyLarge = WidgetBody,
    bodyMedium = TrackArtist,
    bodySmall = WidgetCaption,
    labelLarge = StatusText,
    labelMedium = TrackDuration,
    labelSmall = WidgetCaption
)