package com.example.dou.ui.components.weather

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.WeatherCondition
import com.example.dou.data.model.WeatherInfo
import com.example.dou.ui.theme.*

@Composable
fun WeatherWidget(
    weatherInfo: WeatherInfo,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val iconColor = getWeatherIconColor(weatherInfo.condition)
    
    // Show error if present and temperature is 0 (no valid data)
    if (weatherInfo.errorMessage != null && weatherInfo.temperature == 0) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = "Error",
                tint = AccentYellow,
                modifier = Modifier.size(
                    if (isCompact) Dimens.WeatherIconMedium else Dimens.WeatherIconLarge
                )
            )
            
            Spacer(modifier = Modifier.width(if (isCompact) 16.dp else 20.dp))
            
            Column {
                Text(
                    text = "API Error",
                    style = if (isCompact) TemperatureMedium else TemperatureLarge,
                    color = ClockWhite
                )
                Text(
                    text = weatherInfo.errorMessage,
                    style = WidgetCaption,
                    color = AccentYellow,
                    maxLines = 2
                )
            }
        }
        return
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth(), // No padding - parent handles all padding
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top // Top-align so sun rays match clock top
    ) {
        // Condition Icon
        Icon(
            imageVector = getWeatherIcon(weatherInfo.condition),
            contentDescription = weatherInfo.condition.name,
            tint = iconColor,
            modifier = Modifier.size(
                if (isCompact) Dimens.WeatherIconMedium else Dimens.WeatherIconLarge
            )
        )
        
        Spacer(modifier = Modifier.width(if (isCompact) 16.dp else 20.dp))
        
        // Temperature + Humidity column
        Column {
            Text(
                text = weatherInfo.getTemperatureOnly(),
                style = if (isCompact) TemperatureMedium else TemperatureLarge,
                color = ClockWhite
            )
            
            // Humidity below temperature
            Text(
                text = "${weatherInfo.humidity}% humidity",
                style = WidgetCaption,
                color = MutedGray
            )
        }
    }
}

/**
 * Compact weather for portrait status strip
 */
@Composable
fun WeatherWidgetCompact(
    weatherInfo: WeatherInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ItemSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = getWeatherIcon(weatherInfo.condition),
            contentDescription = weatherInfo.condition.name,
            tint = getWeatherIconColor(weatherInfo.condition),
            modifier = Modifier.size(Dimens.WeatherIconSmall)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = weatherInfo.getFormattedTemperature(),
            style = StatusText,
            color = ClockWhite
        )
    }
}

/**
 * Linearized weather display - single horizontal line
 * Format: [Sun Icon] 23° • 45% Humidity
 * Perfect as a visual divider between sections
 */
@Composable
fun WeatherWidgetLinear(
    weatherInfo: WeatherInfo,
    modifier: Modifier = Modifier
) {
    // Show error if present and temperature is 0 (no valid data)
    if (weatherInfo.errorMessage != null && weatherInfo.temperature == 0) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = "Error",
                tint = AccentYellow,
                modifier = Modifier.size(Dimens.IconSizeSmall)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = weatherInfo.errorMessage.take(40) + if (weatherInfo.errorMessage.length > 40) "..." else "",
                style = WidgetCaption,
                color = AccentYellow
            )
        }
        return
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Weather icon (grayscale for consistency)
        Icon(
            imageVector = getWeatherIcon(weatherInfo.condition),
            contentDescription = weatherInfo.condition.name,
            tint = ClockDim, // Monochrome for stealth look
            modifier = Modifier.size(Dimens.IconSizeSmall)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Temperature
        Text(
            text = weatherInfo.getTemperatureOnly(),
            style = StatusText,
            color = ClockWhite
        )
        
        // Separator
        Text(
            text = "  •  ",
            style = StatusText,
            color = MutedGray
        )
        
        // Humidity
        Text(
            text = "${weatherInfo.humidity}%",
            style = StatusText,
            color = ClockDim
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = "Humidity",
            style = WidgetCaption,
            color = MutedGray
        )
    }
}

@Composable
private fun getWeatherIcon(condition: WeatherCondition): ImageVector {
    return when (condition) {
        WeatherCondition.SUNNY -> Icons.Outlined.WbSunny
        WeatherCondition.PARTLY_CLOUDY -> Icons.Outlined.WbCloudy
        WeatherCondition.CLOUDY -> Icons.Outlined.Cloud
        WeatherCondition.RAINY -> Icons.Outlined.WaterDrop
        WeatherCondition.THUNDERSTORM -> Icons.Outlined.Thunderstorm
        WeatherCondition.SNOWY -> Icons.Outlined.AcUnit
        WeatherCondition.FOGGY -> Icons.Outlined.Cloud // Foggy not available, using Cloud
        WeatherCondition.WINDY -> Icons.Outlined.Air
        WeatherCondition.UNKNOWN -> Icons.Outlined.QuestionMark
    }
}

@Composable
private fun getWeatherIconColor(condition: WeatherCondition): Color {
    return when (condition) {
        WeatherCondition.SUNNY -> AccentYellow
        WeatherCondition.PARTLY_CLOUDY -> ClockWarm
        WeatherCondition.CLOUDY -> ClockDim
        WeatherCondition.RAINY -> AccentBlue
        WeatherCondition.THUNDERSTORM -> AccentPurple
        WeatherCondition.SNOWY -> AccentCyan
        WeatherCondition.FOGGY -> MutedGray
        WeatherCondition.WINDY -> ClockCool
        WeatherCondition.UNKNOWN -> MutedGray
    }
}
