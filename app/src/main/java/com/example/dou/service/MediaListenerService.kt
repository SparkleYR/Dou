package com.example.dou.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.dou.data.repository.NotificationRepository

/**
 * NotificationListenerService required for MediaSessionManager access
 * Also captures active notifications for display as logos
 * 
 * This service must be enabled in Settings > Apps > Special access > Notification access
 * for media session monitoring and notification capture to work.
 */
class MediaListenerService : NotificationListenerService() {
    
    companion object {
        private const val TAG = "MediaListenerService"
        
        // Track if dock is actively being displayed
        @Volatile
        var isDockActive: Boolean = false
    }
    
    private val notificationRepository: NotificationRepository by lazy {
        NotificationRepository.getInstance(applicationContext)
    }
    
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener connected")
        
        // Process existing notifications
        try {
            val activeNotifications = activeNotifications
            activeNotifications?.forEach { sbn ->
                processNotification(sbn)
            }
            Log.d(TAG, "Processed ${activeNotifications?.size ?: 0} existing notifications")
        } catch (e: Exception) {
            Log.e(TAG, "Error processing existing notifications", e)
        }
    }
    
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification listener disconnected")
        notificationRepository.clearAll()
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let { 
            processNotification(it)
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            Log.d(TAG, "Notification removed: ${it.packageName}, key: ${it.key}")
            notificationRepository.onNotificationRemoved(it.key)
        }
    }
    
    private fun processNotification(sbn: StatusBarNotification) {
        // Skip notifications without content
        val notification = sbn.notification ?: return
        
        Log.d(TAG, "Processing notification from: ${sbn.packageName}, key: ${sbn.key}")
        
        notificationRepository.onNotificationPosted(
            key = sbn.key,
            packageName = sbn.packageName
        )
    }
}
