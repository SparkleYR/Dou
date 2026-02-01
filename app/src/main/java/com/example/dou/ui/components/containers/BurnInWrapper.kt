package com.example.dou.ui.components.containers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dou.ui.theme.Dimens
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Wrapper that subtly shifts content position to prevent OLED burn-in
 * Shifts by a random amount every few minutes within safe bounds
 */
@Composable
fun BurnInWrapper(
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableIntStateOf(0) }
    var offsetY by remember { mutableIntStateOf(0) }
    
    // Animate the offset changes smoothly
    val animatedOffsetX by animateIntAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 2000, easing = EaseInOutQuad),
        label = "burnInOffsetX"
    )
    
    val animatedOffsetY by animateIntAsState(
        targetValue = offsetY,
        animationSpec = tween(durationMillis = 2000, easing = EaseInOutQuad),
        label = "burnInOffsetY"
    )
    
    // Periodically shift position
    LaunchedEffect(enabled) {
        if (enabled) {
            while (true) {
                delay(Dimens.BurnInIntervalMs)
                offsetX = Random.nextInt(-10, 11)
                offsetY = Random.nextInt(-5, 6)
            }
        }
    }
    
    Box(
        modifier = modifier.offset {
            if (enabled) {
                IntOffset(animatedOffsetX.dp.roundToPx(), animatedOffsetY.dp.roundToPx())
            } else {
                IntOffset.Zero
            }
        }
    ) {
        content()
    }
}
