package com.example.dou.ui.components.status

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.AlarmOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.AlarmInfo
import com.example.dou.ui.theme.*

/**
 * Alarm indicator showing next alarm time
 * Shows alarm icon with time, or dimmed icon if no alarm set
 */
@Composable
fun AlarmIndicator(
    alarmInfo: AlarmInfo,
    modifier: Modifier = Modifier,
    use24Hour: Boolean = false,
    compact: Boolean = false
) {
    val hasAlarm = alarmInfo.hasAlarm
    val iconColor = if (hasAlarm) ClockWhite else MutedGray
    val textColor = if (hasAlarm) ClockDim else MutedGray
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            imageVector = if (hasAlarm) Icons.Outlined.Alarm else Icons.Outlined.AlarmOff,
            contentDescription = "Alarm",
            tint = iconColor,
            modifier = Modifier.size(if (compact) Dimens.IconSizeSmall else Dimens.IconSizeMedium)
        )
        
        Spacer(modifier = Modifier.width(if (compact) 4.dp else 8.dp))
        
        Text(
            text = if (hasAlarm) alarmInfo.getFormattedTime(use24Hour) else "No alarm",
            style = if (compact) StatusTextSmall else StatusText,
            color = textColor
        )
    }
}
