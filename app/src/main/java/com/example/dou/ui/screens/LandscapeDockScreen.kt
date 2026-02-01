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
import com.example.dou.ui.components.music.MusicWidgetLandscape
import com.example.dou.ui.components.notifications.NotificationIconsCompact
import com.example.dou.ui.components.status.BatteryIndicator
import com.example.dou.ui.components.weather.WeatherWidget
import com.example.dou.ui.theme.*

/**
 * Landscape dock layout - BALANCED SPLIT SCREEN DESIGN
 * 
 * ┌─────────────────────────────┬─────────────────────────────┐
 * │      LEFT COLUMN (55%)      │     RIGHT COLUMN (45%)      │
 * │                             │                             │
 * │          10:42              │  ┌───────────────────────┐  │
 * │     Saturday, Jan 31        │  │      WEATHER          │  │
 * │                             │  │      ☀️ 23°C          │  │
 * │                             │  └───────────────────────┘  │
 * │     🔋 85%  •  ⏰ 7:30      │  ┌───────────────────────┐  │
 * │                             │  │      MUSIC            │  │
 * │     (left-aligned below     │  │   Track • Artist      │  │
 * │      the date)              │  │        ▶️             │  │
 * │                             │  └───────────────────────┘  │
 * └─────────────────────────────┴─────────────────────────────┘
 * 
 * Key fixes:
 * - Balanced 55/45 split (not extreme 65/35)
 * - Left column: Clock, Date, Status all left-aligned
 * - Right column: Widgets stacked vertically
 * - Center of gravity aligned between columns
 */
@Composable
fun LandscapeDockScreen(
    dockState: DockState,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BurnInWrapper(enabled = dockState.settings.burnInPrevention) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(TrueBlack)
                .padding(
                    horizontal = Dimens.ScreenPaddingHorizontal,
                    vertical = Dimens.ScreenPaddingVertical
                ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.GridGap),
            verticalAlignment = Alignment.Top  // Both columns start at same top line
        ) {
            // ═══════════════════════════════════════════════════════════
            // LEFT COLUMN (55%): Clock, Date, Status
            // TOP-ALIGNED to match right column (weather aligns with clock)
            // ═══════════════════════════════════════════════════════════
            Column(
                modifier = Modifier
                    .weight(Dimens.LANDSCAPE_LEFT_WEIGHT)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                // Clock
                DigitalClock(
                    time = dockState.getFormattedTime(),
                    amPm = dockState.getAmPm(),
                    style = ClockDisplayStyle.LARGE
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Date - left aligned under clock
                DateDisplay(
                    date = dockState.getFormattedDate(),
                    isLarge = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Status Row: Battery • Alarm (left-aligned, below date)
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Battery
                    BatteryIndicator(
                        batteryState = dockState.batteryState,
                        compact = false,
                        showIcon = true
                    )
                    
                    if (dockState.settings.showAlarm && dockState.alarmInfo.hasAlarm) {
                        // Separator dot
                        Text(
                            text = "    •    ",
                            style = StatusText,
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
                                modifier = Modifier.size(Dimens.IconSizeMedium)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dockState.alarmInfo.getFormattedTime(dockState.settings.use24HourFormat),
                                style = StatusText,
                                color = ClockDim
                            )
                        }
                    }
                }
                
                // Notification Icons (below status, left-aligned)
                if (dockState.settings.showNotifications && dockState.notificationsState.hasNotifications) {
                    Spacer(modifier = Modifier.height(20.dp))
                    NotificationIconsCompact(
                        notificationsState = dockState.notificationsState
                    )
                }
            }
            
            // ═══════════════════════════════════════════════════════════
            // RIGHT COLUMN (45%): Weather + Music stacked
            // Pushed down to align with clock top (accounting for font baseline)
            // ═══════════════════════════════════════════════════════════
            Column(
                modifier = Modifier
                    .weight(Dimens.LANDSCAPE_RIGHT_WEIGHT)
                    .fillMaxHeight()
                    .padding(top = 12.dp), // Push down to align with clock numbers
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start
            ) {
                // Weather Widget (compact, no weight - uses intrinsic size)
                if (dockState.settings.showWeather) {
                    WeatherWidget(
                        weatherInfo = dockState.weatherInfo,
                        isCompact = true
                    )
                }
                
                // Music Widget (ALWAYS VISIBLE for debugging)
                MusicWidgetLandscape(
                    mediaInfo = dockState.mediaInfo,
                    onPlayPauseClick = onPlayPauseClick
                )
            }
        }
    }
}
