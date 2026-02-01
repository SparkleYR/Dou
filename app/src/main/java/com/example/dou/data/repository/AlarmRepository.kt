package com.example.dou.data.repository

import android.app.AlarmManager
import android.content.Context
import android.provider.Settings
import com.example.dou.data.model.AlarmInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Repository for fetching next scheduled alarm information
 */
class AlarmRepository(private val context: Context) {
    
    private val alarmManager: AlarmManager? = 
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    
    /**
     * Flow that periodically checks for the next alarm
     * Updates every minute
     */
    val nextAlarmFlow: Flow<AlarmInfo> = flow {
        while (true) {
            emit(getNextAlarm())
            kotlinx.coroutines.delay(60_000) // Check every minute
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get the next scheduled alarm
     */
    fun getNextAlarm(): AlarmInfo {
        return try {
            val nextAlarmClock = alarmManager?.nextAlarmClock
            
            if (nextAlarmClock != null) {
                val triggerTime = nextAlarmClock.triggerTime
                val alarmTime = Instant.ofEpochMilli(triggerTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                
                // Format the time
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                val formattedTime = alarmTime.format(timeFormatter)
                
                // Calculate if it's today, tomorrow, or later
                val now = LocalDateTime.now()
                val label = when {
                    alarmTime.toLocalDate() == now.toLocalDate() -> "Today"
                    alarmTime.toLocalDate() == now.toLocalDate().plusDays(1) -> "Tomorrow"
                    else -> alarmTime.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
                }
                
                AlarmInfo(
                    time = LocalTime.of(alarmTime.hour, alarmTime.minute),
                    label = label,
                    isEnabled = true,
                    triggerTimeMillis = triggerTime
                )
            } else {
                // Try reading from settings as fallback
                getAlarmFromSettings()
            }
        } catch (e: Exception) {
            AlarmInfo(isEnabled = false)
        }
    }
    
    /**
     * Fallback: Read alarm from system settings
     */
    private fun getAlarmFromSettings(): AlarmInfo {
        return try {
            val nextAlarmString = Settings.System.getString(
                context.contentResolver,
                Settings.System.NEXT_ALARM_FORMATTED
            )
            
            if (!nextAlarmString.isNullOrEmpty()) {
                // Parse the formatted string (format varies by device)
                AlarmInfo(
                    time = LocalTime.now(), // Placeholder
                    label = nextAlarmString,
                    isEnabled = true
                )
            } else {
                AlarmInfo(isEnabled = false)
            }
        } catch (e: Exception) {
            AlarmInfo(isEnabled = false)
        }
    }
    
    /**
     * Check if there's an upcoming alarm within the next 24 hours
     */
    fun hasUpcomingAlarm(): Boolean {
        val alarm = getNextAlarm()
        if (!alarm.isEnabled || alarm.triggerTimeMillis == null) return false
        
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L
        
        return alarm.triggerTimeMillis in now..(now + twentyFourHours)
    }
}
