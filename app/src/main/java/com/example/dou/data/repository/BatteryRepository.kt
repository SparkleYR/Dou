package com.example.dou.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.dou.data.model.BatteryState
import com.example.dou.data.model.ChargingStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Repository for monitoring real-time battery status
 */
class BatteryRepository(private val context: Context) {
    
    /**
     * Flow that emits battery state changes in real-time
     */
    val batteryState: Flow<BatteryState> = callbackFlow {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val batteryState = parseBatteryIntent(it)
                    trySend(batteryState)
                }
            }
        }
        
        // Register for battery change broadcasts
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        
        // Get initial battery state
        val initialIntent = context.registerReceiver(batteryReceiver, filter)
        initialIntent?.let {
            trySend(parseBatteryIntent(it))
        }
        
        awaitClose {
            context.unregisterReceiver(batteryReceiver)
        }
    }.distinctUntilChanged()
    
    /**
     * Get current battery state synchronously
     */
    fun getCurrentBatteryState(): BatteryState {
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        return batteryIntent?.let { parseBatteryIntent(it) } ?: BatteryState()
    }
    
    private fun parseBatteryIntent(intent: Intent): BatteryState {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level >= 0 && scale > 0) {
            (level * 100 / scale)
        } else {
            0
        }
        
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val chargingStatus = when {
            status == BatteryManager.BATTERY_STATUS_CHARGING -> ChargingStatus.CHARGING
            status == BatteryManager.BATTERY_STATUS_FULL -> ChargingStatus.FULL
            else -> ChargingStatus.DISCHARGING
        }
        
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val isPluggedIn = plugged == BatteryManager.BATTERY_PLUGGED_AC ||
                plugged == BatteryManager.BATTERY_PLUGGED_USB ||
                plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f
        
        return BatteryState(
            percentage = percentage,
            chargingStatus = chargingStatus,
            isPluggedIn = isPluggedIn,
            temperature = temperature,
            isLowBattery = percentage <= 20
        )
    }
    
    /**
     * Check if device is currently charging
     */
    fun isCharging(): Boolean {
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }
}
