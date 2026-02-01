package com.example.dou.ui.components.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.dou.data.model.NotificationsState
import com.example.dou.ui.theme.Dimens

// Grayscale color matrix for "stealth" look
private val grayscaleMatrix = ColorMatrix().apply { setToSaturation(0f) }
private val grayscaleFilter = ColorFilter.colorMatrix(grayscaleMatrix)

/**
 * Displays notification app icons in a horizontal row
 * Shows only unique apps (deduplicated), max 5 icons
 * Icons are desaturated (grayscale) for the stealth AMOLED aesthetic
 */
@Composable
fun NotificationIconsRow(
    notificationsState: NotificationsState,
    modifier: Modifier = Modifier
) {
    if (!notificationsState.hasNotifications) return
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        notificationsState.uniqueAppNotifications.forEach { notification ->
            notification.appIcon?.let { drawable ->
                val bitmap = drawable.toBitmap(48, 48)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "App notification",
                    modifier = Modifier
                        .size(Dimens.NotificationIconSize)
                        .clip(RoundedCornerShape(8.dp)),
                    colorFilter = grayscaleFilter // Desaturate for stealth look
                )
            }
        }
    }
}

/**
 * Compact version for landscape mode
 * Icons are desaturated (grayscale) for the stealth AMOLED aesthetic
 */
@Composable
fun NotificationIconsCompact(
    notificationsState: NotificationsState,
    modifier: Modifier = Modifier
) {
    if (!notificationsState.hasNotifications) return
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        notificationsState.uniqueAppNotifications.take(3).forEach { notification ->
            notification.appIcon?.let { drawable ->
                val bitmap = drawable.toBitmap(36, 36)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "App notification",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    colorFilter = grayscaleFilter // Desaturate for stealth look
                )
            }
        }
    }
}
