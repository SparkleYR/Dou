package com.example.dou.data.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Combined state for the entire dock display
 */
data class DockState(
    val currentTime: LocalTime = LocalTime.now(),
    val currentDate: LocalDate = LocalDate.now(),
    val batteryState: BatteryState = BatteryState(),
    val alarmInfo: AlarmInfo = AlarmInfo(),
    val weatherInfo: WeatherInfo = WeatherInfo(),
    val mediaInfo: MediaInfo = MediaInfo(),
    val notificationsState: NotificationsState = NotificationsState(),
    val settings: DockSettings = DockSettings()
) {
    fun getFormattedTime(): String {
        return if (settings.use24HourFormat) {
            String.format("%02d:%02d", currentTime.hour, currentTime.minute)
        } else {
            val hour = if (currentTime.hour == 0) 12 
                       else if (currentTime.hour > 12) currentTime.hour - 12 
                       else currentTime.hour
            String.format("%d:%02d", hour, currentTime.minute)
        }
    }
    
    fun getAmPm(): String? {
        return if (!settings.use24HourFormat) {
            if (currentTime.hour < 12) "AM" else "PM"
        } else null
    }
    
    fun getFormattedDate(): String {
        val dayOfWeek = currentDate.dayOfWeek.name.lowercase()
            .replaceFirstChar { it.uppercase() }
        val month = currentDate.month.name.lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3)
        val day = currentDate.dayOfMonth
        
        return "$dayOfWeek, $month $day"
    }
}

/**
 * User preferences for dock behavior
 */
data class DockSettings(
    val use24HourFormat: Boolean = false,
    val showSeconds: Boolean = false,
    val showWeather: Boolean = true,
    val showMusic: Boolean = true,
    val showAlarm: Boolean = true,
    val showNotifications: Boolean = true,
    val autoBrightness: Boolean = true,
    val burnInPrevention: Boolean = true,
    val accentColor: AccentColorOption = AccentColorOption.BLUE,
    val weatherApiKey: String = ""
)

enum class AccentColorOption {
    BLUE,
    GREEN,
    ORANGE,
    PURPLE,
    PINK,
    CYAN,
    YELLOW
}
