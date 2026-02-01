package com.example.dou.ui.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.DockState
import com.example.dou.ui.components.clock.ClockDisplayStyle
import com.example.dou.ui.components.clock.DateDisplay
import com.example.dou.ui.components.clock.DigitalClock
import com.example.dou.ui.components.status.AlarmIndicator
import com.example.dou.ui.components.status.BatteryIndicator
import com.example.dou.ui.theme.Dimens
import com.example.dou.ui.theme.SoftBlack

/**
 * Primary display container for landscape mode (left 65%)
 * Contains: Clock (center), Date (below), Battery (bottom-left), Alarm (bottom-right)
 */
@Composable
fun PrimaryContainerLandscape(
    dockState: DockState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(Dimens.ContainerPadding)
    ) {
        // Center content: Clock and Date
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DigitalClock(
                time = dockState.getFormattedTime(),
                amPm = dockState.getAmPm(),
                style = ClockDisplayStyle.LARGE
            )
            
            DateDisplay(
                date = dockState.getFormattedDate(),
                isLarge = true
            )
        }
        
        // Bottom-left: Battery indicator
        BatteryIndicator(
            batteryState = dockState.batteryState,
            modifier = Modifier.align(Alignment.BottomStart),
            compact = false
        )
        
        // Bottom-right: Next alarm
        if (dockState.settings.showAlarm) {
            AlarmIndicator(
                alarmInfo = dockState.alarmInfo,
                modifier = Modifier.align(Alignment.BottomEnd),
                use24Hour = dockState.settings.use24HourFormat,
                compact = false
            )
        }
    }
}

/**
 * Primary display container for portrait mode (top 40%)
 * Contains: Clock (center), Date (below clock, within box)
 */
@Composable
fun PrimaryContainerPortrait(
    dockState: DockState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.ContainerPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DigitalClock(
                time = dockState.getFormattedTime(),
                amPm = dockState.getAmPm(),
                style = ClockDisplayStyle.MEDIUM
            )
            
            DateDisplay(
                date = dockState.getFormattedDate(),
                isLarge = false
            )
        }
    }
}
