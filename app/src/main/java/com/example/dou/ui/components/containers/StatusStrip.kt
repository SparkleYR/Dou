package com.example.dou.ui.components.containers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.DockState
import com.example.dou.ui.components.status.AlarmIndicator
import com.example.dou.ui.components.status.BatteryIndicator
import com.example.dou.ui.components.weather.WeatherWidgetCompact
import com.example.dou.ui.theme.Dimens
import com.example.dou.ui.theme.DimGray

/**
 * Status strip for portrait mode (middle 20%)
 * Left 50%: Weather (centered icon + temp)
 * Right 50%: System (Battery % + Next Alarm)
 * 
 * NOTE: This component is now replaced by inline status in PortraitDockScreen
 * Kept for backwards compatibility
 */
@Composable
fun StatusStripPortrait(
    dockState: DockState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ContainerPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.GridGap)
    ) {
        // Left Box - Weather
        if (dockState.settings.showWeather) {
            Box(
                modifier = Modifier
                    .weight(Dimens.WIDGET_SPLIT_WEIGHT)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                WeatherWidgetCompact(weatherInfo = dockState.weatherInfo)
            }
        }
        
        // Right Box - System Status (Battery + Alarm)
        Box(
            modifier = Modifier
                .weight(Dimens.WIDGET_SPLIT_WEIGHT)
                .fillMaxHeight()
                .padding(Dimens.WidgetPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxSize()
            ) {
                // Top: Battery
                BatteryIndicator(
                    batteryState = dockState.batteryState,
                    compact = true
                )
                
                // Bottom: Alarm
                if (dockState.settings.showAlarm) {
                    AlarmIndicator(
                        alarmInfo = dockState.alarmInfo,
                        use24Hour = dockState.settings.use24HourFormat,
                        compact = true
                    )
                }
            }
        }
    }
}
