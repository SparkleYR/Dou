package com.example.dou.ui.components.clock

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dou.ui.theme.ClockDim
import com.example.dou.ui.theme.DateDisplayLarge
import com.example.dou.ui.theme.DateDisplayMedium

/**
 * Date display component showing day of week, month, and day
 * Example: "Saturday, Jan 31"
 */
@Composable
fun DateDisplay(
    date: String,
    modifier: Modifier = Modifier,
    color: Color = ClockDim,
    isLarge: Boolean = true
) {
    Text(
        text = date,
        style = if (isLarge) DateDisplayLarge else DateDisplayMedium,
        color = color,
        modifier = modifier.padding(top = 8.dp)
    )
}
