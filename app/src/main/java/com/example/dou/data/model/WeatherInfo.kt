package com.example.dou.data.model

data class WeatherInfo(
    val temperature: Int = 0,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val condition: WeatherCondition = WeatherCondition.SUNNY,
    val humidity: Int = 0,
    val location: String = "",
    val highTemp: Int = 0,
    val lowTemp: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
) {
    fun getFormattedTemperature(): String {
        val symbol = when (temperatureUnit) {
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.FAHRENHEIT -> "°F"
        }
        return "$temperature$symbol"
    }
    
    fun getTemperatureOnly(): String = "$temperature°"
}

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

enum class WeatherCondition {
    SUNNY,
    PARTLY_CLOUDY,
    CLOUDY,
    RAINY,
    THUNDERSTORM,
    SNOWY,
    FOGGY,
    WINDY,
    UNKNOWN;
    
    companion object {
        val CLEAR = SUNNY
        val RAIN = RAINY
        val SNOW = SNOWY
        val FOG = FOGGY
        val NIGHT_CLEAR = SUNNY
        val NIGHT_CLOUDY = CLOUDY
    }
}
