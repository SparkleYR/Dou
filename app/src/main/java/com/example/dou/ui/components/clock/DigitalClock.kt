package com.example.dou.ui.components.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dou.ui.theme.*

/**
 * Large digital clock display for dock mode
 * Shows time in HH:MM format with optional AM/PM indicator
 */
@Composable
fun DigitalClock(
    time: String,
    amPm: String? = null,
    modifier: Modifier = Modifier,
    color: Color = ClockWhite,
    style: ClockDisplayStyle = ClockDisplayStyle.LARGE
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = time,
            style = when (style) {
                ClockDisplayStyle.LARGE -> ClockDisplayLarge
                ClockDisplayStyle.MEDIUM -> ClockDisplayMedium
                ClockDisplayStyle.SMALL -> ClockDisplaySmall
            },
            color = color
        )
        
        // AM/PM indicator for 12-hour format
        if (amPm != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = amPm,
                style = TextStyle(
                    fontSize = when (style) {
                        ClockDisplayStyle.LARGE -> 32.sp
                        ClockDisplayStyle.MEDIUM -> 28.sp
                        ClockDisplayStyle.SMALL -> 24.sp
                    },
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp
                ),
                color = color.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = when (style) {
                    ClockDisplayStyle.LARGE -> 20.dp
                    ClockDisplayStyle.MEDIUM -> 16.dp
                    ClockDisplayStyle.SMALL -> 12.dp
                })
            )
        }
    }
}

enum class ClockDisplayStyle {
    LARGE,   // 120sp - Full screen landscape
    MEDIUM,  // 96sp - Portrait or compact
    SMALL    // 72sp - Minimal space
}
