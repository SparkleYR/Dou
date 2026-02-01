package com.example.dou.data.model

/**
 * Represents the current battery state of the device
 */
data class BatteryState(
    val percentage: Int = 100,
    val chargingStatus: ChargingStatus = ChargingStatus.DISCHARGING,
    val isPluggedIn: Boolean = false,
    val temperature: Float = 0f,
    val isLowBattery: Boolean = false,
    // Legacy compatibility
    val isCharging: Boolean = false,
    val chargingType: ChargingType = ChargingType.NONE,
    val estimatedTimeToFull: Long? = null // in minutes
) {
    val isLow: Boolean get() = percentage <= 20 || isLowBattery
    val isMedium: Boolean get() = percentage in 21..50
    val isGood: Boolean get() = percentage > 50
    val isFull: Boolean get() = percentage >= 100 || chargingStatus == ChargingStatus.FULL
    
    // Check if actually charging (from either new or old field)
    val isCurrentlyCharging: Boolean 
        get() = isCharging || chargingStatus == ChargingStatus.CHARGING || chargingStatus == ChargingStatus.FULL
}

enum class ChargingStatus {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING
}

enum class ChargingType {
    NONE,
    AC,         // Wall charger
    USB,        // USB port
    WIRELESS    // Qi wireless
}
