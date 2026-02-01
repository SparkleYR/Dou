package com.example.dou.data.model

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Represents the next scheduled alarm
 */
data class AlarmInfo(
    val time: LocalTime? = null,
    val label: String? = null,
    val isEnabled: Boolean = false,
    val triggerTimeMillis: Long? = null,
    // Legacy compatibility with LocalDateTime
    val dateTime: LocalDateTime? = null
) {
    val hasAlarm: Boolean get() = (time != null || dateTime != null) && isEnabled
    
    fun getFormattedTime(use24Hour: Boolean = false): String {
        // Prefer LocalTime, fallback to LocalDateTime
        val hour: Int
        val minute: Int
        
        when {
            time != null -> {
                hour = time.hour
                minute = time.minute
            }
            dateTime != null -> {
                hour = dateTime.hour
                minute = dateTime.minute
            }
            else -> return "--:--"
        }
        
        return if (use24Hour) {
            String.format("%02d:%02d", hour, minute)
        } else {
            val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            val amPm = if (hour < 12) "AM" else "PM"
            String.format("%d:%02d %s", displayHour, minute, amPm)
        }
    }
}
