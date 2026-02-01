package com.example.dou.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.dou.MainActivity
import com.example.dou.R

/**
 * BroadcastReceiver that listens for power connection events
 * and shows a notification to start dock mode
 */
class PowerConnectionReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "PowerConnectionReceiver"
        const val CHANNEL_ID = "dock_mode_channel"
        const val NOTIFICATION_ID = 1001
        
        /**
         * Create the notification channel (required for Android 8.0+)
         */
        fun createNotificationChannel(context: Context) {
            val name = "Dock Mode"
            val descriptionText = "Notifications for starting dock mode when charging"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(false)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) 
                as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        Log.d(TAG, "Received power event: ${intent.action}")
        
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                Log.d(TAG, "Power connected - showing dock notification")
                showDockModeNotification(context)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d(TAG, "Power disconnected - dismissing notification")
                dismissNotification(context)
            }
        }
    }
    
    private fun showDockModeNotification(context: Context) {
        // Ensure channel exists
        createNotificationChannel(context)
        
        // Check notification permission (Android 13+)
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification permission not granted")
            return
        }
        
        // Create intent to launch MainActivity
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentTitle("Charging Detected")
            .setContentText("Tap to start Dock Mode")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Your device is now charging. Tap to launch the beautiful dock display with clock, weather, and music controls."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_media_play,
                "Start Dock",
                pendingIntent
            )
            .build()
        
        // Show the notification
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            Log.d(TAG, "Dock mode notification shown")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to show notification: permission denied", e)
        }
    }
    
    private fun dismissNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
