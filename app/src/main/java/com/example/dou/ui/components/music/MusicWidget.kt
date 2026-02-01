package com.example.dou.ui.components.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.MediaInfo
import com.example.dou.ui.theme.*

/**
 * Music widget for landscape mode - SMARTWATCH STYLE
 * 
 * Ultra-minimal design:
 * ┌────────┐  Song Title
 * │ Album  │  Artist Name
 * │  Art   │  |◀  (▶)  ▶|    <- Ghost controls + circular progress
 * └────────┘
 */
@Composable
fun MusicWidgetLandscape(
    mediaInfo: MediaInfo,
    onPlayPauseClick: () -> Unit,
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Album Art (left side, aligned with weather icon above)
        AlbumArtThumbnail(
            albumArt = mediaInfo.albumArt,
            size = AlbumArtSize.LARGE
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Track Info + Smartwatch Controls (stacked vertically)
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            // Track Title (Medium weight for hierarchy)
            Text(
                text = mediaInfo.displayTitle,
                style = TrackTitleBold,
                color = ClockWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Artist (Light weight)
            if (mediaInfo.displayArtist.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = mediaInfo.displayArtist,
                    style = TrackArtistLight,
                    color = ClockDim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp)) // Tight - buttons hug the text
            
            // Smartwatch-style controls: Ghost Prev + Circular Play + Ghost Next
            SmartWatchControls(
                isPlaying = mediaInfo.isCurrentlyPlaying,
                progress = mediaInfo.progress,
                onPlayPauseClick = onPlayPauseClick,
                onSkipNext = onSkipNext,
                onSkipPrevious = onSkipPrevious,
                enabled = mediaInfo.displayTitle != "Not Playing"
            )
        }
    }
}

/**
 * Smartwatch-style controls with circular progress ring around play button
 * and ghost (50% opacity) prev/next buttons - visible but not competing
 */
@Composable
fun SmartWatchControls(
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSkipPrevious,
            enabled = enabled,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Previous",
                tint = ClockWhite.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
        
        CircularPlayButton(
            isPlaying = isPlaying,
            progress = progress,
            onClick = onPlayPauseClick,
            enabled = enabled
        )
        
        IconButton(
            onClick = onSkipNext,
            enabled = enabled,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Next",
                tint = ClockWhite.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Play/Pause button with circular progress ring around it
 * The ring fills up as the song plays (smartwatch style)
 * 
 * Design: Thin elegant ring (2dp) matching clock font weight
 */
@Composable
fun CircularPlayButton(
    isPlaying: Boolean,
    progress: Float,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val ringSize = 52.dp
    val strokeWidth = 2.dp
    
    Box(
        modifier = modifier
            .size(ringSize)
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = ClockWhite.copy(alpha = 0.15f),
            strokeWidth = strokeWidth
        )
        
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = ClockWhite,
            strokeWidth = strokeWidth
        )
        
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = if (enabled) ClockWhite else MutedGray,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * Music widget for portrait mode
 * 
 * COMPACT LAYOUT with PLAY OVERLAY on Album Art:
 * ┌────────┐  Song Title
 * │  ▶   │  Artist Name        <- Play button INSIDE album art
 * └────────┘  0:00 / 3:45
 * 
 * This saves vertical space and centers the play button within its own block
 */
@Composable
fun MusicWidgetPortrait(
    mediaInfo: MediaInfo,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.WidgetPadding, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Album Art with Play Button Overlay
        AlbumArtWithPlayOverlay(
            albumArt = mediaInfo.albumArt,
            isPlaying = mediaInfo.isCurrentlyPlaying,
            onPlayPauseClick = onPlayPauseClick,
            enabled = mediaInfo.displayTitle != "Not Playing"
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Track Info + Progress (stacked)
        Column {
            // Track Title
            Text(
                text = mediaInfo.displayTitle,
                style = TrackTitleBold,
                color = ClockWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Artist
            if (mediaInfo.displayArtist.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = mediaInfo.displayArtist,
                    style = TrackArtistLight,
                    color = ClockDim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress time
            Text(
                text = mediaInfo.getFormattedProgress(),
                style = TrackDuration,
                color = MutedGray
            )
        }
    }
}

/**
 * Album art with semi-transparent play button overlay
 */
@Composable
fun AlbumArtWithPlayOverlay(
    albumArt: android.graphics.Bitmap?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(Dimens.AlbumArtSizeMedium)
            .clip(RoundedCornerShape(Dimens.AlbumArtCornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        // Album art background
        if (albumArt != null) {
            Image(
                bitmap = albumArt.asImageBitmap(),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MutedGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MusicNote,
                    contentDescription = "Music",
                    tint = MutedGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        // Semi-transparent play button overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TrueBlack.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onPlayPauseClick,
                enabled = enabled,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = ClockWhite,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Album art thumbnail with placeholder
 */
@Composable
fun AlbumArtThumbnail(
    albumArt: android.graphics.Bitmap?,
    size: AlbumArtSize = AlbumArtSize.MEDIUM,
    modifier: Modifier = Modifier
) {
    val thumbnailSize = when (size) {
        AlbumArtSize.LARGE -> Dimens.AlbumArtSizeLarge
        AlbumArtSize.MEDIUM -> Dimens.AlbumArtSizeMedium
        AlbumArtSize.SMALL -> Dimens.AlbumArtSizeSmall
    }
    
    Box(
        modifier = modifier
            .size(thumbnailSize)
            .clip(RoundedCornerShape(Dimens.AlbumArtCornerRadius))
            .background(MutedGray.copy(alpha = 0.3f)), // CRITICAL: Add background so it's visible
        contentAlignment = Alignment.Center
    ) {
        if (albumArt != null) {
            Image(
                bitmap = albumArt.asImageBitmap(),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.MusicNote,
                contentDescription = "No Album Art",
                tint = MutedGray,
                modifier = Modifier.size(thumbnailSize / 2)
            )
        }
    }
}

enum class AlbumArtSize {
    LARGE, MEDIUM, SMALL
}

enum class PlayButtonSize {
    LARGE, MEDIUM, SMALL
}

/**
 * Play/Pause toggle button with configurable size
 */
@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: PlayButtonSize = PlayButtonSize.LARGE
) {
    val buttonSize = when (size) {
        PlayButtonSize.LARGE -> Dimens.PlayButtonSize
        PlayButtonSize.MEDIUM -> 44.dp
        PlayButtonSize.SMALL -> 36.dp
    }
    
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(buttonSize)
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = if (enabled) ClockWhite else MutedGray,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Progress bar for track position
 */
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .height(Dimens.ProgressBarHeight)
            .clip(RoundedCornerShape(Dimens.ProgressBarCornerRadius)),
        color = ProgressActive,
        trackColor = ProgressTrack
    )
}
