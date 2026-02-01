package com.example.dou.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.Log
import com.example.dou.data.model.NotificationInfo
import com.example.dou.data.model.NotificationsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing notification state
 * Works with MediaListenerService to track active notifications
 */
class NotificationRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "NotificationRepository"
        
        // Singleton instance for service communication
        @Volatile
        private var INSTANCE: NotificationRepository? = null
        
        fun getInstance(context: Context): NotificationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotificationRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
    
    private val _notificationsState = MutableStateFlow(NotificationsState())
    val notificationsState: StateFlow<NotificationsState> = _notificationsState.asStateFlow()
    
    // Packages to exclude (system apps, this app, media players)
    private val excludedPackages = setOf(
        context.packageName,
        "android",
        "com.android.systemui",
        "com.android.settings",
        // Common media players (already shown in music widget)
        "com.spotify.music",
        "com.google.android.apps.youtube.music",
        "com.google.android.youtube",
        "com.amazon.mp3",
        "com.apple.android.music",
        "com.soundcloud.android",
        "com.pandora.android",
        "deezer.android.app"
    )
    
    /**
     * Called when a notification is posted
     */
    fun onNotificationPosted(key: String, packageName: String) {
        if (packageName in excludedPackages) return
        
        try {
            val appIcon = getAppIcon(packageName)
            val notification = NotificationInfo(
                key = key,
                packageName = packageName,
                appIcon = appIcon,
                timestamp = System.currentTimeMillis()
            )
            
            val currentList = _notificationsState.value.notifications.toMutableList()
            // Remove existing notification with same key
            currentList.removeAll { it.key == key }
            currentList.add(notification)
            
            _notificationsState.value = NotificationsState(currentList)
            Log.d(TAG, "Notification posted: $packageName, total: ${currentList.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding notification", e)
        }
    }
    
    /**
     * Called when a notification is removed
     */
    fun onNotificationRemoved(key: String) {
        try {
            val currentList = _notificationsState.value.notifications.toMutableList()
            currentList.removeAll { it.key == key }
            _notificationsState.value = NotificationsState(currentList)
            Log.d(TAG, "Notification removed, remaining: ${currentList.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error removing notification", e)
        }
    }
    
    /**
     * Clear all notifications
     */
    fun clearAll() {
        _notificationsState.value = NotificationsState()
    }
    
    /**
     * Get app icon for a package
     */
    private fun getAppIcon(packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "App icon not found for: $packageName")
            null
        }
    }
    
    /**
     * Check if notification listener permission is enabled
     */
    fun isNotificationListenerEnabled(): Boolean {
        val packageName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return flat?.contains(packageName) == true
    }
}
