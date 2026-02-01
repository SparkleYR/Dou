package com.example.dou.ui.components.status

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.outlined.Battery0Bar
import androidx.compose.material.icons.outlined.Battery2Bar
import androidx.compose.material.icons.outlined.Battery4Bar
import androidx.compose.material.icons.outlined.Battery6Bar
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.BatteryState
import com.example.dou.ui.theme.*

/**
 * Battery indicator showing percentage and charging status
 * Displays charging icon when plugged in, otherwise shows battery level icon
 * 
 * COLOR LOGIC (Full Monochrome Pro):
 * - White/Gray: ALWAYS (no green, no distractions)
 * - Red: Only when critically low (<15%)
 * 
 * The album art should be the ONLY color on screen
 */
@Composable
fun BatteryIndicator(
    batteryState: BatteryState,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    compact: Boolean = false
) {
    // FULL MONOCHROME - only red for critical, otherwise white/gray
    val batteryColor = when {
        batteryState.percentage < 15 -> BatteryLow // Red warning only for critical
        else -> ClockDim // Always monochrome - album art is the star
    }
    
    val batteryIcon = getBatteryIcon(batteryState)
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (showIcon) {
            Icon(
                imageVector = batteryIcon,
                contentDescription = if (batteryState.isCurrentlyCharging) "Charging" else "Battery",
                tint = batteryColor,
                modifier = Modifier.size(if (compact) Dimens.IconSizeSmall else Dimens.IconSizeMedium)
            )
            Spacer(modifier = Modifier.width(if (compact) 4.dp else 8.dp))
        }
        
        Text(
            text = "${batteryState.percentage}%",
            style = if (compact) StatusTextSmall else StatusText,
            color = batteryColor
        )
    }
}

@Composable
private fun getBatteryIcon(batteryState: BatteryState): ImageVector {
    return when {
        batteryState.isCurrentlyCharging -> Icons.Default.BatteryChargingFull
        batteryState.percentage >= 90 -> Icons.Outlined.BatteryFull
        batteryState.percentage >= 70 -> Icons.Outlined.Battery6Bar
        batteryState.percentage >= 50 -> Icons.Outlined.Battery4Bar
        batteryState.percentage >= 20 -> Icons.Outlined.Battery2Bar
        else -> Icons.Outlined.Battery0Bar
    }
}
