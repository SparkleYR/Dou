package com.example.dou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.DockState
import com.example.dou.ui.components.clock.ClockDisplayStyle
import com.example.dou.ui.components.clock.DateDisplay
import com.example.dou.ui.components.clock.DigitalClock
import com.example.dou.ui.components.containers.BurnInWrapper
import com.example.dou.ui.components.music.MusicWidgetPortrait
import com.example.dou.ui.components.notifications.NotificationIconsRow
import com.example.dou.ui.components.status.BatteryIndicator
import com.example.dou.ui.components.weather.WeatherWidgetLinear
import com.example.dou.ui.theme.*

/**
 * Portrait dock layout - EQUAL RHYTHM DESIGN
 * 
 * 4 GROUPS with EQUAL 40dp gaps:
 * ┌──────────────────────────────────────┐
 * │  GROUP 1: Clock + Date              │
 * │              10:42                   │
 * │         Saturday, Jan 31             │
 * │                                      │  ← 40dp gap
 * │  GROUP 2: Status + Notifications    │
 * │      🔋 85%    •    ⏰ 7:30 AM       │
 * │         📱  💬  📧                   │
 * │                                      │  ← 40dp gap
 * │  GROUP 3: Weather                   │
 * │    ☀️ 23° • 45% Humidity             │
 * │                                      │  ← 40dp gap
 * │  GROUP 4: Music                     │
 * │         [🎵▶️]  Track Title           │  ← Play on album art
 * │                Artist                │
 * └──────────────────────────────────────┘
 */
@Composable
fun PortraitDockScreen(
    dockState: DockState,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BurnInWrapper(enabled = dockState.settings.burnInPrevention) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(TrueBlack)
                .padding(
                    horizontal = Dimens.ScreenPaddingHorizontal,
                    vertical = Dimens.ScreenPaddingVertical
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically)
        ) {
            // ═══════════════════════════════════════════════════════════
            // GROUP 1: Clock + Date (tightly coupled)
            // ═══════════════════════════════════════════════════════════
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Clock
                DigitalClock(
                    time = dockState.getFormattedTime(),
                    amPm = dockState.getAmPm(),
                    style = ClockDisplayStyle.MEDIUM
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Date
                DateDisplay(
                    date = dockState.getFormattedDate(),
                    isLarge = false
                )
            }
            
            // ═══════════════════════════════════════════════════════════
            // GROUP 2: Status + Notifications (tightly coupled)
            // ═══════════════════════════════════════════════════════════
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Row: Battery • Alarm (inline, centered)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Battery
                    BatteryIndicator(
                        batteryState = dockState.batteryState,
                        compact = true,
                        showIcon = true
                    )
                    
                    if (dockState.settings.showAlarm && dockState.alarmInfo.hasAlarm) {
                        // Separator dot
                        Text(
                            text = "  •  ",
                            style = StatusTextSmall,
                            color = MutedGray
                        )
                        
                        // Alarm
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Alarm,
                                contentDescription = "Alarm",
                                tint = ClockDim,
                                modifier = Modifier.size(Dimens.IconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dockState.alarmInfo.getFormattedTime(dockState.settings.use24HourFormat),
                                style = StatusTextSmall,
                                color = ClockDim
                            )
                        }
                    }
                }
                
                // Notification Icons (grayscale, below status)
                if (dockState.settings.showNotifications && dockState.notificationsState.hasNotifications) {
                    Spacer(modifier = Modifier.height(12.dp))
                    NotificationIconsRow(
                        notificationsState = dockState.notificationsState
                    )
                }
            }
            
            // ═══════════════════════════════════════════════════════════
            // GROUP 3: Weather
            // ═══════════════════════════════════════════════════════════
            if (dockState.settings.showWeather) {
                WeatherWidgetLinear(
                    weatherInfo = dockState.weatherInfo
                )
            }
            
            // ═══════════════════════════════════════════════════════════
            // GROUP 4: Music (compact with play overlay on album art)
            // ═══════════════════════════════════════════════════════════
            if (dockState.settings.showMusic) {
                MusicWidgetPortrait(
                    mediaInfo = dockState.mediaInfo,
                    onPlayPauseClick = onPlayPauseClick
                )
            }
        }
    }
}
