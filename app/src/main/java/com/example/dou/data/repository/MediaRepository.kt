package com.example.dou.data.repository

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Log
import com.example.dou.data.model.MediaInfo
import com.example.dou.data.model.PlaybackStatus
import com.example.dou.service.MediaListenerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * Repository for monitoring and controlling media playback
 * Uses MediaSessionManager to interact with active media sessions
 */
class MediaRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "MediaRepository"
    }
    
    private val mediaSessionManager: MediaSessionManager? = 
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    
    private var activeController: MediaController? = null
    
    /**
     * Flow that emits media state changes
     * Updates at 1Hz when playing for smooth progress bar updates
     */
    val mediaState: Flow<MediaInfo> = callbackFlow {
        val componentName = ComponentName(context, MediaListenerService::class.java)
        var pollingJob: Job? = null
        
        // Start 1Hz polling for position updates when playing
        fun startPolling() {
            pollingJob?.cancel()
            pollingJob = launch {
                while (true) {
                    delay(1000L) // 1Hz update rate
                    activeController?.let { controller ->
                        if (controller.playbackState?.state == PlaybackState.STATE_PLAYING) {
                            trySend(getMediaInfoFromController(controller))
                        }
                    }
                }
            }
        }
        
        val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
            Log.d(TAG, "Active sessions changed: ${controllers?.size ?: 0} sessions")
            updateActiveController(controllers)
            activeController?.let { controller ->
                trySend(getMediaInfoFromController(controller))
            } ?: trySend(MediaInfo())
        }
        
        val mediaCallback = object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) {
                activeController?.let { trySend(getMediaInfoFromController(it)) }
            }
            
            override fun onMetadataChanged(metadata: MediaMetadata?) {
                activeController?.let { trySend(getMediaInfoFromController(it)) }
            }
            
            override fun onSessionDestroyed() {
                trySend(MediaInfo())
            }
        }
        
        try {
            // Check if notification listener permission is granted
            if (isNotificationListenerEnabled()) {
                mediaSessionManager?.addOnActiveSessionsChangedListener(
                    sessionListener,
                    componentName
                )
                
                // Get initial sessions
                val controllers = mediaSessionManager?.getActiveSessions(componentName)
                updateActiveController(controllers)
                activeController?.let { controller ->
                    controller.registerCallback(mediaCallback)
                    trySend(getMediaInfoFromController(controller))
                } ?: trySend(MediaInfo())
                
                // Start 1Hz polling for position updates
                startPolling()
            } else {
                Log.w(TAG, "Notification listener permission not granted")
                trySend(MediaInfo())
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: Notification listener permission required", e)
            trySend(MediaInfo())
        }
        
        awaitClose {
            try {
                pollingJob?.cancel()
                mediaSessionManager?.removeOnActiveSessionsChangedListener(sessionListener)
                activeController?.unregisterCallback(mediaCallback)
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up media listeners", e)
            }
        }
    }
    
    private fun updateActiveController(controllers: List<MediaController>?) {
        // Prefer playing controller, otherwise use first available
        activeController = controllers?.firstOrNull { controller ->
            controller.playbackState?.state == PlaybackState.STATE_PLAYING
        } ?: controllers?.firstOrNull()
    }
    
    private fun getMediaInfoFromController(controller: MediaController): MediaInfo {
        val metadata = controller.metadata
        val playbackState = controller.playbackState
        
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
        val album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""
        val albumArtUri = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val position = playbackState?.position ?: 0L
        
        // Try to get album art bitmap directly from metadata
        val albumArtBitmap: Bitmap? = try {
            metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get album art bitmap", e)
            null
        }
        
        val status = when (playbackState?.state) {
            PlaybackState.STATE_PLAYING -> PlaybackStatus.PLAYING
            PlaybackState.STATE_PAUSED -> PlaybackStatus.PAUSED
            PlaybackState.STATE_STOPPED -> PlaybackStatus.STOPPED
            PlaybackState.STATE_BUFFERING -> PlaybackStatus.PLAYING
            else -> PlaybackStatus.STOPPED
        }
        
        return MediaInfo(
            trackTitle = title,
            artistName = artist,
            albumName = album,
            albumArtUrl = albumArtUri,
            albumArt = albumArtBitmap,
            duration = duration,
            currentPosition = position,
            playbackStatus = status
        )
    }
    
    /**
     * Toggle play/pause on active media session
     */
    fun togglePlayPause() {
        activeController?.let { controller ->
            val state = controller.playbackState?.state
            when (state) {
                PlaybackState.STATE_PLAYING -> controller.transportControls.pause()
                PlaybackState.STATE_PAUSED -> controller.transportControls.play()
                else -> controller.transportControls.play()
            }
        }
    }
    
    /**
     * Skip to next track
     */
    fun skipNext() {
        activeController?.transportControls?.skipToNext()
    }
    
    /**
     * Skip to previous track
     */
    fun skipPrevious() {
        activeController?.transportControls?.skipToPrevious()
    }
    
    /**
     * Seek to position (in milliseconds)
     */
    fun seekTo(position: Long) {
        activeController?.transportControls?.seekTo(position)
    }
    
    /**
     * Get current media info synchronously
     */
    fun getCurrentMediaInfo(): MediaInfo {
        return activeController?.let { getMediaInfoFromController(it) } ?: MediaInfo()
    }
    
    /**
     * Check if notification listener service is enabled
     */
    fun isNotificationListenerEnabled(): Boolean {
        val packageName = context.packageName
        val flat = android.provider.Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return flat?.contains(packageName) == true
    }
}
