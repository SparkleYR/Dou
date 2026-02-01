package com.example.dou.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dou.data.datastore.UserPreferences
import com.example.dou.data.model.*
import com.example.dou.data.repository.AlarmRepository
import com.example.dou.data.repository.BatteryRepository
import com.example.dou.data.repository.MediaRepository
import com.example.dou.data.repository.NotificationRepository
import com.example.dou.data.repository.WeatherRepository
import com.example.dou.receiver.PowerConnectionReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class DockViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "DockViewModel"
    }
    
    // Repositories
    private val userPreferences = UserPreferences(application)
    private val batteryRepository = BatteryRepository(application)
    private val alarmRepository = AlarmRepository(application)
    private val weatherRepository = WeatherRepository(application)
    private val mediaRepository = MediaRepository(application)
    private val notificationRepository = NotificationRepository.getInstance(application)
    
    // State
    private val _dockState = MutableStateFlow(DockState())
    val dockState: StateFlow<DockState> = _dockState.asStateFlow()
    
    private val _isLandscape = MutableStateFlow(false)
    val isLandscape: StateFlow<Boolean> = _isLandscape.asStateFlow()
    
    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()
    
    // Permission states
    private val _needsNotificationListenerPermission = MutableStateFlow(false)
    val needsNotificationListenerPermission: StateFlow<Boolean> = _needsNotificationListenerPermission.asStateFlow()
    
    init {
        // Create notification channel for charging notifications
        PowerConnectionReceiver.createNotificationChannel(application)
        
        // Initialize all data streams
        loadSettings()
        startClock()
        collectBatteryState()
        collectAlarmInfo()
        collectMediaState()
        collectNotificationsState()
        fetchWeather()
        
        // Check permission states
        checkPermissions()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            userPreferences.settingsFlow
                .catch { e -> Log.e(TAG, "Error loading settings", e) }
                .collect { settings ->
                    val previousApiKey = _dockState.value.settings.weatherApiKey
                    _dockState.update { it.copy(settings = settings) }
                    
                    // Update weather API key when settings change
                    weatherRepository.setApiKey(settings.weatherApiKey)
                    
                    Log.d(TAG, "Settings loaded. API key length: ${settings.weatherApiKey.length}, previous: ${previousApiKey.length}")
                    
                    // If API key is not empty, trigger weather fetch
                    // Always fetch if key exists (even on first load)
                    if (settings.weatherApiKey.isNotEmpty()) {
                        Log.d(TAG, "API key present, triggering weather refresh")
                        refreshWeatherNow()
                    }
                }
        }
    }
    
    /**
     * Force an immediate weather refresh (used when API key changes or on startup)
     */
    private fun refreshWeatherNow() {
        viewModelScope.launch {
            Log.d(TAG, "refreshWeatherNow() called")
            try {
                val result = weatherRepository.fetchWeather()
                result.onSuccess { weatherInfo ->
                    _dockState.update { it.copy(weatherInfo = weatherInfo) }
                    Log.d(TAG, "Weather refreshed successfully: ${weatherInfo.temperature}° ${weatherInfo.condition}")
                }
                result.onFailure { e ->
                    Log.e(TAG, "Weather refresh failed: ${e.message}", e)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Weather refresh exception", e)
            }
        }
    }
    
    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                _dockState.update { state ->
                    state.copy(
                        currentTime = LocalTime.now(),
                        currentDate = LocalDate.now()
                    )
                }
                // Update every second (1Hz) for music playback sync
                delay(1000L)
            }
        }
    }
    
    private fun collectBatteryState() {
        viewModelScope.launch {
            batteryRepository.batteryState
                .catch { e -> Log.e(TAG, "Error collecting battery state", e) }
                .collect { batteryState ->
                    _dockState.update { it.copy(batteryState = batteryState) }
                }
        }
    }
    
    private fun collectAlarmInfo() {
        viewModelScope.launch {
            alarmRepository.nextAlarmFlow
                .catch { e -> Log.e(TAG, "Error collecting alarm info", e) }
                .collect { alarmInfo ->
                    _dockState.update { it.copy(alarmInfo = alarmInfo) }
                }
        }
    }
    
    private fun collectMediaState() {
        viewModelScope.launch {
            mediaRepository.mediaState
                .catch { e -> Log.e(TAG, "Error collecting media state", e) }
                .collect { mediaInfo ->
                    _dockState.update { it.copy(mediaInfo = mediaInfo) }
                }
        }
    }
    
    private fun collectNotificationsState() {
        viewModelScope.launch {
            notificationRepository.notificationsState
                .catch { e -> Log.e(TAG, "Error collecting notifications state", e) }
                .collect { notificationsState ->
                    _dockState.update { it.copy(notificationsState = notificationsState) }
                }
        }
    }
    
    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                // Initial fetch
                val result = weatherRepository.fetchWeather()
                result.onSuccess { weatherInfo ->
                    _dockState.update { it.copy(weatherInfo = weatherInfo) }
                }
                result.onFailure { e ->
                    Log.e(TAG, "Weather fetch failed", e)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Weather fetch exception", e)
            }
            
            // Periodic updates (every 15 minutes)
            while (true) {
                delay(WeatherRepository.UPDATE_INTERVAL)
                try {
                    val result = weatherRepository.fetchWeather()
                    result.onSuccess { weatherInfo ->
                        _dockState.update { it.copy(weatherInfo = weatherInfo) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Weather update failed", e)
                }
            }
        }
    }
    
    private fun checkPermissions() {
        _needsNotificationListenerPermission.value = !mediaRepository.isNotificationListenerEnabled()
    }
    
    // Public actions
    
    fun updateOrientation(isLandscape: Boolean) {
        _isLandscape.value = isLandscape
    }
    
    fun togglePlayPause() {
        mediaRepository.togglePlayPause()
    }
    
    fun skipNext() {
        mediaRepository.skipNext()
    }
    
    fun skipPrevious() {
        mediaRepository.skipPrevious()
    }
    
    fun seekTo(position: Long) {
        mediaRepository.seekTo(position)
    }
    
    fun refreshWeather() {
        viewModelScope.launch {
            try {
                val result = weatherRepository.forceRefresh()
                result.onSuccess { weatherInfo ->
                    _dockState.update { it.copy(weatherInfo = weatherInfo) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Weather refresh failed", e)
            }
        }
    }
    
    fun updateSettings(settings: DockSettings) {
        viewModelScope.launch {
            userPreferences.updateSettings(settings)
        }
        _dockState.update { it.copy(settings = settings) }
    }
    
    fun showSettings() {
        _showSettings.value = true
    }
    
    fun hideSettings() {
        _showSettings.value = false
    }
    
    fun refreshPermissionState() {
        checkPermissions()
    }
}
