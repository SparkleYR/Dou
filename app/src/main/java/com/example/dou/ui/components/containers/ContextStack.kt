package com.example.dou.ui.components.containers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.DockState
import com.example.dou.ui.components.music.MusicWidgetLandscape
import com.example.dou.ui.components.weather.WeatherWidget
import com.example.dou.ui.theme.Dimens
import com.example.dou.ui.theme.DimGray

/**
 * Context stack for landscape mode (right 35%)
 * Top: Weather widget (40% height)
 * Bottom: Music widget (60% height)
 */
@Composable
fun ContextStackLandscape(
    dockState: DockState,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = Dimens.ContainerPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.GridGap)
    ) {
        // Weather Widget - Top 40%
        if (dockState.settings.showWeather) {
            Box(
                modifier = Modifier
                    .weight(Dimens.WEATHER_HEIGHT_WEIGHT)
                    .fillMaxWidth()
            ) {
                WeatherWidget(
                    weatherInfo = dockState.weatherInfo,
                    isCompact = true
                )
            }
        }
        
        // Music Widget - Bottom 60%
        if (dockState.settings.showMusic) {
            Box(
                modifier = Modifier
                    .weight(Dimens.MUSIC_HEIGHT_WEIGHT)
                    .fillMaxWidth()
            ) {
                MusicWidgetLandscape(
                    mediaInfo = dockState.mediaInfo,
                    onPlayPauseClick = onPlayPauseClick
                )
            }
        }
    }
}
