package com.example.dou.data.model

import android.graphics.Bitmap

/**
 * Represents the currently playing media
 */
data class MediaInfo(
    val trackTitle: String = "Not Playing",
    val artistName: String = "",
    val albumName: String = "",
    val albumArtUrl: String? = null,
    val albumArt: Bitmap? = null,
    val duration: Long = 0L,           // Total duration in ms
    val currentPosition: Long = 0L,    // Current position in ms
    val playbackStatus: PlaybackStatus = PlaybackStatus.STOPPED,
    // Legacy compatibility
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val albumArtUri: String? = null,
    val position: Long = 0L,
    val isPlaying: Boolean = false,
    val hasMedia: Boolean = false
) {
    val progress: Float
        get() = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f
    
    // Check if currently playing
    val isCurrentlyPlaying: Boolean
        get() = isPlaying || playbackStatus == PlaybackStatus.PLAYING
    
    // Get display title (prefer new field, fallback to legacy)
    val displayTitle: String
        get() = trackTitle.ifEmpty { title.ifEmpty { "Not Playing" } }
    
    // Get display artist
    val displayArtist: String
        get() = artistName.ifEmpty { artist }
    
    // Get display album
    val displayAlbum: String
        get() = albumName.ifEmpty { album }
    
    // Get album art url
    val displayAlbumArtUrl: String?
        get() = albumArtUrl ?: albumArtUri
    
    // Get position
    val displayPosition: Long
        get() = if (currentPosition > 0) currentPosition else position
    
    fun getFormattedDuration(): String = formatTime(duration)
    
    fun getFormattedPosition(): String = formatTime(displayPosition)
    
    fun getFormattedProgress(): String = "${getFormattedPosition()} / ${getFormattedDuration()}"
    
    private fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}

enum class PlaybackStatus {
    PLAYING,
    PAUSED,
    STOPPED,
    BUFFERING
}
