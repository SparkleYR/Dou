package com.example.dou.data.model

import android.graphics.drawable.Drawable

/**
 * Represents an active notification for display in the dock
 * Only stores the app icon for minimal display
 */
data class NotificationInfo(
    val key: String,
    val packageName: String,
    val appIcon: Drawable? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Container for all active notifications (as logos only)
 */
data class NotificationsState(
    val notifications: List<NotificationInfo> = emptyList()
) {
    val hasNotifications: Boolean
        get() = notifications.isNotEmpty()
    
    val count: Int
        get() = notifications.size
    
    // Get unique app notifications (deduplicated by package name)
    val uniqueAppNotifications: List<NotificationInfo>
        get() = notifications
            .groupBy { it.packageName }
            .map { (_, notifs) -> notifs.maxByOrNull { it.timestamp }!! }
            .sortedByDescending { it.timestamp }
            .take(5) // Show max 5 app icons
}
