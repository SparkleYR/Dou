package com.example.dou.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.dou.data.model.AccentColorOption
import com.example.dou.data.model.DockSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dou_settings")

/**
 * Manages user preferences using DataStore
 */
class UserPreferences(private val context: Context) {
    
    companion object {
        // Preference keys
        val USE_24_HOUR_FORMAT = booleanPreferencesKey("use_24_hour_format")
        val SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val SHOW_WEATHER = booleanPreferencesKey("show_weather")
        val SHOW_MUSIC = booleanPreferencesKey("show_music")
        val SHOW_ALARM = booleanPreferencesKey("show_alarm")
        val SHOW_NOTIFICATIONS = booleanPreferencesKey("show_notifications")
        val AUTO_BRIGHTNESS = booleanPreferencesKey("auto_brightness")
        val BURN_IN_PREVENTION = booleanPreferencesKey("burn_in_prevention")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val WEATHER_API_KEY = stringPreferencesKey("weather_api_key")
    }
    
    /**
     * Get settings as a Flow
     */
    val settingsFlow: Flow<DockSettings> = context.dataStore.data.map { preferences ->
        DockSettings(
            use24HourFormat = preferences[USE_24_HOUR_FORMAT] ?: false,
            showSeconds = preferences[SHOW_SECONDS] ?: false,
            showWeather = preferences[SHOW_WEATHER] ?: true,
            showMusic = preferences[SHOW_MUSIC] ?: true,
            showAlarm = preferences[SHOW_ALARM] ?: true,
            showNotifications = preferences[SHOW_NOTIFICATIONS] ?: true,
            autoBrightness = preferences[AUTO_BRIGHTNESS] ?: true,
            burnInPrevention = preferences[BURN_IN_PREVENTION] ?: true,
            accentColor = try {
                AccentColorOption.valueOf(preferences[ACCENT_COLOR] ?: "BLUE")
            } catch (e: Exception) {
                AccentColorOption.BLUE
            },
            weatherApiKey = preferences[WEATHER_API_KEY] ?: ""
        )
    }
    
    /**
     * Update 24-hour format preference
     */
    suspend fun setUse24HourFormat(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_24_HOUR_FORMAT] = value
        }
    }
    
    /**
     * Update show seconds preference
     */
    suspend fun setShowSeconds(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_SECONDS] = value
        }
    }
    
    /**
     * Update show weather preference
     */
    suspend fun setShowWeather(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_WEATHER] = value
        }
    }
    
    /**
     * Update show music preference
     */
    suspend fun setShowMusic(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_MUSIC] = value
        }
    }
    
    /**
     * Update show alarm preference
     */
    suspend fun setShowAlarm(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_ALARM] = value
        }
    }
    
    /**
     * Update auto brightness preference
     */
    suspend fun setAutoBrightness(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_BRIGHTNESS] = value
        }
    }
    
    /**
     * Update burn-in prevention preference
     */
    suspend fun setBurnInPrevention(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BURN_IN_PREVENTION] = value
        }
    }
    
    /**
     * Update accent color preference
     */
    suspend fun setAccentColor(color: AccentColorOption) {
        context.dataStore.edit { preferences ->
            preferences[ACCENT_COLOR] = color.name
        }
    }
    
    /**
     * Update all settings at once
     */
    suspend fun updateSettings(settings: DockSettings) {
        context.dataStore.edit { preferences ->
            preferences[USE_24_HOUR_FORMAT] = settings.use24HourFormat
            preferences[SHOW_SECONDS] = settings.showSeconds
            preferences[SHOW_WEATHER] = settings.showWeather
            preferences[SHOW_MUSIC] = settings.showMusic
            preferences[SHOW_ALARM] = settings.showAlarm
            preferences[SHOW_NOTIFICATIONS] = settings.showNotifications
            preferences[AUTO_BRIGHTNESS] = settings.autoBrightness
            preferences[BURN_IN_PREVENTION] = settings.burnInPrevention
            preferences[ACCENT_COLOR] = settings.accentColor.name
            preferences[WEATHER_API_KEY] = settings.weatherApiKey
        }
    }
}
