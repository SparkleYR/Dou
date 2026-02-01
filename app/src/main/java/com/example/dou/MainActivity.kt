package com.example.dou

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dou.data.model.DockState
import com.example.dou.service.MediaListenerService
import com.example.dou.ui.screens.DockScreen
import com.example.dou.ui.screens.SettingsScreen
import com.example.dou.ui.theme.DouTheme
import com.example.dou.ui.theme.TrueBlack
import com.example.dou.viewmodel.DockViewModel

class MainActivity : ComponentActivity() {
    
    private var notificationManager: NotificationManager? = null
    private var previousInterruptionFilter: Int = NotificationManager.INTERRUPTION_FILTER_ALL
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setupImmersiveMode()
        checkDndAccess()
        
        setContent {
            DouTheme {
                DockApp()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        MediaListenerService.isDockActive = true
        enableFocusMode()
    }
    
    override fun onPause() {
        super.onPause()
        MediaListenerService.isDockActive = false
        disableFocusMode()
    }
    
    private fun checkDndAccess() {
        notificationManager?.let { nm ->
            if (!nm.isNotificationPolicyAccessGranted) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
    }
    
    private fun enableFocusMode() {
        notificationManager?.let { nm ->
            if (nm.isNotificationPolicyAccessGranted) {
                previousInterruptionFilter = nm.currentInterruptionFilter
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            }
        }
    }
    
    private fun disableFocusMode() {
        notificationManager?.let { nm ->
            if (nm.isNotificationPolicyAccessGranted) {
                nm.setInterruptionFilter(previousInterruptionFilter)
            }
        }
    }
    
    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupImmersiveMode()
        }
    }
}

@Composable
fun DockApp(
    viewModel: DockViewModel = viewModel()
) {
    val dockState by viewModel.dockState.collectAsState()
    val showSettings by viewModel.showSettings.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TrueBlack)
    ) {
        AnimatedContent(
            targetState = showSettings,
            transitionSpec = {
                if (targetState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "ScreenTransition"
        ) { settingsVisible ->
            if (settingsVisible) {
                SettingsScreen(
                    settings = dockState.settings,
                    onSettingsChange = { viewModel.updateSettings(it) },
                    onBackClick = { viewModel.hideSettings() }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { viewModel.showSettings() }
                            )
                        }
                ) {
                    DockScreen(
                        dockState = dockState,
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 400,
    name = "Landscape Preview"
)
@Composable
fun DockPreviewLandscape() {
    DouTheme {
        DockScreen(
            dockState = DockState(),
            onPlayPauseClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 800,
    name = "Portrait Preview"
)
@Composable
fun DockPreviewPortrait() {
    DouTheme {
        DockScreen(
            dockState = DockState(),
            onPlayPauseClick = {}
        )
    }
}