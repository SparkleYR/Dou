package com.example.dou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.dou.data.model.AccentColorOption
import com.example.dou.data.model.DockSettings
import com.example.dou.ui.theme.*

/**
 * Settings screen for customizing dock appearance and behavior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: DockSettings,
    onSettingsChange: (DockSettings) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TrueBlack)
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Settings",
                    color = ClockWhite
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ClockWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TrueBlack
            )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ═══════════════════════════════════════════════════════════
            // Clock Settings
            // ═══════════════════════════════════════════════════════════
            SettingsSectionHeader(title = "Clock")
            
            SettingsToggleItem(
                icon = Icons.Outlined.Schedule,
                title = "24-hour format",
                subtitle = "Display time in 24-hour format",
                checked = settings.use24HourFormat,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(use24HourFormat = it))
                }
            )
            
            SettingsToggleItem(
                icon = Icons.Outlined.Timer,
                title = "Show seconds",
                subtitle = "Display seconds in clock (uses more battery)",
                checked = settings.showSeconds,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(showSeconds = it))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ═══════════════════════════════════════════════════════════
            // Widget Settings
            // ═══════════════════════════════════════════════════════════
            SettingsSectionHeader(title = "Widgets")
            
            SettingsToggleItem(
                icon = Icons.Outlined.WbSunny,
                title = "Show weather",
                subtitle = "Display weather widget",
                checked = settings.showWeather,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(showWeather = it))
                }
            )
            
            SettingsToggleItem(
                icon = Icons.Outlined.MusicNote,
                title = "Show music",
                subtitle = "Display music player widget",
                checked = settings.showMusic,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(showMusic = it))
                }
            )
            
            SettingsToggleItem(
                icon = Icons.Outlined.Alarm,
                title = "Show alarm",
                subtitle = "Display next alarm time",
                checked = settings.showAlarm,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(showAlarm = it))
                }
            )
            
            SettingsToggleItem(
                icon = Icons.Outlined.Notifications,
                title = "Show notifications",
                subtitle = "Display notification app icons",
                checked = settings.showNotifications,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(showNotifications = it))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ═══════════════════════════════════════════════════════════
            // Weather API Settings
            // ═══════════════════════════════════════════════════════════
            SettingsSectionHeader(title = "Weather API")
            
            WeatherApiKeyInput(
                apiKey = settings.weatherApiKey,
                onApiKeyChange = { newKey ->
                    onSettingsChange(settings.copy(weatherApiKey = newKey))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ═══════════════════════════════════════════════════════════
            // Display Settings
            // ═══════════════════════════════════════════════════════════
            SettingsSectionHeader(title = "Display")
            
            SettingsToggleItem(
                icon = Icons.Outlined.BrightnessAuto,
                title = "Auto brightness",
                subtitle = "Automatically adjust screen brightness",
                checked = settings.autoBrightness,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(autoBrightness = it))
                }
            )
            
            SettingsToggleItem(
                icon = Icons.Outlined.ScreenRotation,
                title = "Burn-in prevention",
                subtitle = "Subtly shift content to protect AMOLED screen",
                checked = settings.burnInPrevention,
                onCheckedChange = { 
                    onSettingsChange(settings.copy(burnInPrevention = it))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ═══════════════════════════════════════════════════════════
            // Accent Color
            // ═══════════════════════════════════════════════════════════
            SettingsSectionHeader(title = "Accent Color")
            
            AccentColorPicker(
                selectedColor = settings.accentColor,
                onColorSelected = { 
                    onSettingsChange(settings.copy(accentColor = it))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ═══════════════════════════════════════════════════════════
            // About & Info
            // ═══════════════════════════════════════════════════════════
            SettingsSectionHeader(title = "Information")
            
            SettingsInfoItem(
                icon = Icons.Outlined.Notifications,
                title = "Music Controls",
                subtitle = "Requires Notification Listener permission",
                infoText = "Enable in Settings → Apps → Special access"
            )
            
            SettingsInfoItem(
                icon = Icons.Outlined.LocationOn,
                title = "Weather Location",
                subtitle = "Requires Location permission for local weather",
                infoText = "Grant permission when prompted"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ═══════════════════════════════════════════════════════════
            // Usage Tips
            // ═══════════════════════════════════════════════════════════
            SettingsSectionHeader(title = "Tips")
            
            SettingsInfoItem(
                icon = Icons.Outlined.TouchApp,
                title = "Open Settings",
                subtitle = "Double-tap anywhere on the dock screen",
                infoText = ""
            )
            
            SettingsInfoItem(
                icon = Icons.Outlined.BatteryChargingFull,
                title = "Charging Notification",
                subtitle = "When you plug in your device, you'll get a notification to start Dock mode",
                infoText = ""
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = AccentBlue,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ClockDim,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = ClockWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MutedGray
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AccentBlue,
                checkedTrackColor = AccentBlue.copy(alpha = 0.3f),
                uncheckedThumbColor = MutedGray,
                uncheckedTrackColor = DimGray
            )
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    infoText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = ClockWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MutedGray
            )
            if (infoText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = infoText,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentBlue
                )
            }
        }
    }
}

@Composable
private fun AccentColorPicker(
    selectedColor: AccentColorOption,
    onColorSelected: (AccentColorOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorOptions = listOf(
        AccentColorOption.BLUE to AccentBlue,
        AccentColorOption.GREEN to AccentGreen,
        AccentColorOption.ORANGE to AccentOrange,
        AccentColorOption.PURPLE to AccentPurple,
        AccentColorOption.PINK to AccentPink,
        AccentColorOption.CYAN to AccentCyan,
        AccentColorOption.YELLOW to AccentYellow
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colorOptions.forEach { (option, color) ->
            ColorCircle(
                color = color,
                isSelected = selectedColor == option,
                onClick = { onColorSelected(option) }
            )
        }
    }
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = ClockWhite,
                        shape = CircleShape
                    )
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Selected",
                tint = TrueBlack,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Weather API key input field with show/hide toggle
 */
@Composable
private fun WeatherApiKeyInput(
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showKey by remember { mutableStateOf(false) }
    var tempKey by remember(apiKey) { mutableStateOf(apiKey) }
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Key,
                contentDescription = null,
                tint = ClockDim,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "OpenWeather API Key",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ClockWhite
                )
                Text(
                    text = "Get free key at openweathermap.org",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentBlue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = tempKey,
            onValueChange = { tempKey = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Enter your API key",
                    color = MutedGray
                )
            },
            visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Row {
                    IconButton(onClick = { showKey = !showKey }) {
                        Icon(
                            imageVector = if (showKey) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (showKey) "Hide" else "Show",
                            tint = MutedGray
                        )
                    }
                    if (tempKey != apiKey && tempKey.isNotEmpty()) {
                        IconButton(onClick = {
                            onApiKeyChange(tempKey)
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Save",
                                tint = AccentGreen
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (tempKey.isNotEmpty()) {
                        onApiKeyChange(tempKey)
                    }
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ClockWhite,
                unfocusedTextColor = ClockDim,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = DimGray,
                cursorColor = AccentBlue,
                focusedContainerColor = TrueBlack,
                unfocusedContainerColor = TrueBlack
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        if (apiKey.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "✓ API key saved",
                style = MaterialTheme.typography.bodySmall,
                color = AccentGreen
            )
        }
    }
}
