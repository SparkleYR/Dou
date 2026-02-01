package com.example.dou.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.dou.data.api.RetrofitClient
import com.example.dou.data.api.WeatherResponse
import com.example.dou.data.model.WeatherCondition
import com.example.dou.data.model.WeatherInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WeatherRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "WeatherRepository"
        private const val DEFAULT_CITY = "New York"
        const val UPDATE_INTERVAL = 15 * 60 * 1000L
    }
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val _weatherState = MutableStateFlow(WeatherInfo())
    val weatherState: StateFlow<WeatherInfo> = _weatherState.asStateFlow()
    
    private var lastUpdateTime = 0L
    private var currentApiKey: String = ""
    
    /**
     * Update the API key from settings
     */
    fun setApiKey(apiKey: String) {
        Log.d(TAG, "setApiKey called, new length: ${apiKey.length}, current length: ${currentApiKey.length}")
        if (apiKey != currentApiKey) {
            currentApiKey = apiKey
            // Reset last update time to force refresh with new key
            lastUpdateTime = 0L
            Log.d(TAG, "API key updated, cache cleared")
        }
    }
    
    /**
     * Fetch current weather data
     * Uses location if permission granted, otherwise falls back to default city
     */
    suspend fun fetchWeather(): Result<WeatherInfo> = withContext(Dispatchers.IO) {
        Log.d(TAG, "fetchWeather called, API key length: ${currentApiKey.length}")
        
        // Check if API key is configured
        if (currentApiKey.isEmpty()) {
            Log.w(TAG, "Weather API key not configured")
            return@withContext Result.failure(Exception("API key not configured. Add it in Settings."))
        }
        
        try {
            // Check if we should skip update (within interval)
            val currentTime = System.currentTimeMillis()
            val timeSinceUpdate = currentTime - lastUpdateTime
            Log.d(TAG, "Time since last update: ${timeSinceUpdate}ms, interval: ${UPDATE_INTERVAL}ms")
            
            if (timeSinceUpdate < UPDATE_INTERVAL && _weatherState.value.temperature != 0) {
                Log.d(TAG, "Using cached weather data")
                return@withContext Result.success(_weatherState.value)
            }
            
            Log.d(TAG, "Fetching fresh weather data...")
            val response: WeatherResponse = if (hasLocationPermission()) {
                try {
                    val location = getCurrentLocation()
                    if (location != null) {
                        RetrofitClient.weatherApiService.getCurrentWeather(
                            latitude = location.first,
                            longitude = location.second,
                            apiKey = currentApiKey
                        )
                    } else {
                        fetchByCity(DEFAULT_CITY)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Location fetch failed, using default city", e)
                    fetchByCity(DEFAULT_CITY)
                }
            } else {
                fetchByCity(DEFAULT_CITY)
            }
            
            val weatherInfo = mapResponseToWeatherInfo(response)
            _weatherState.value = weatherInfo
            lastUpdateTime = currentTime
            
            Result.success(weatherInfo)
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "Weather HTTP error: ${e.code()}", e)
            val errorMessage = when (e.code()) {
                401 -> "Invalid API key. If newly created, wait 10-30 min for activation."
                404 -> "City not found"
                429 -> "API rate limit exceeded"
                else -> "HTTP error: ${e.code()}"
            }
            // Update state with error for UI to display
            _weatherState.value = _weatherState.value.copy(errorMessage = errorMessage)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e(TAG, "Weather fetch failed", e)
            _weatherState.value = _weatherState.value.copy(errorMessage = e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    private suspend fun fetchByCity(city: String): WeatherResponse {
        return RetrofitClient.weatherApiService.getCurrentWeatherByCity(
            cityName = city,
            apiKey = currentApiKey
        )
    }
    
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!hasLocationPermission()) return null
        
        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            
            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(Pair(location.latitude, location.longitude))
                    } else {
                        continuation.resume(null)
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Location fetch failed", exception)
                    continuation.resume(null)
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception getting location", e)
                continuation.resume(null)
            }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
    
    private fun mapResponseToWeatherInfo(response: WeatherResponse): WeatherInfo {
        val weather = response.weather.firstOrNull()
        val condition = mapWeatherCondition(weather?.id ?: 0)
        
        return WeatherInfo(
            temperature = response.main.temp.toInt(),
            condition = condition,
            humidity = response.main.humidity ?: 0,
            location = response.name ?: "",
            highTemp = response.main.temp_max?.toInt() ?: response.main.temp.toInt(),
            lowTemp = response.main.temp_min?.toInt() ?: response.main.temp.toInt()
        )
    }
    
    /**
     * Maps OpenWeather condition codes to our WeatherCondition enum
     * https://openweathermap.org/weather-conditions
     */
    private fun mapWeatherCondition(code: Int): WeatherCondition {
        return when (code) {
            in 200..232 -> WeatherCondition.THUNDERSTORM
            in 300..321 -> WeatherCondition.RAINY
            in 500..531 -> WeatherCondition.RAINY
            in 600..622 -> WeatherCondition.SNOWY
            in 701..781 -> WeatherCondition.FOGGY
            800 -> WeatherCondition.SUNNY
            801 -> WeatherCondition.PARTLY_CLOUDY
            in 802..804 -> WeatherCondition.CLOUDY
            else -> WeatherCondition.UNKNOWN
        }
    }
    
    /**
     * Force refresh weather data (ignores update interval)
     */
    suspend fun forceRefresh(): Result<WeatherInfo> = withContext(Dispatchers.IO) {
        lastUpdateTime = 0L
        fetchWeather()
    }
}
