package com.example.dou.ui.screens

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.dou.data.model.DockState

/**
 * Main dock screen that switches between landscape and portrait layouts
 * based on device orientation
 */
@Composable
fun DockScreen(
    dockState: DockState,
    onPlayPauseClick: () -> Unit,
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    if (isLandscape) {
        LandscapeDockScreen(
            dockState = dockState,
            onPlayPauseClick = onPlayPauseClick,
            onSkipNext = onSkipNext,
            onSkipPrevious = onSkipPrevious,
            modifier = modifier
        )
    } else {
        PortraitDockScreen(
            dockState = dockState,
            onPlayPauseClick = onPlayPauseClick,
            modifier = modifier
        )
    }
}
