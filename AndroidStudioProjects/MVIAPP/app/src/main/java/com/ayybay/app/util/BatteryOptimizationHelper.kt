package com.ayybay.app.util

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Helper class for handling permission requests required for reliable Adhan alarms
 *
 * CRITICAL PERMISSIONS:
 * 1. SCHEDULE_EXACT_ALARM - Required for precise prayer time alarms
 * 2. Battery Optimization Exemption - Prevents background restrictions
 * 3. POST_NOTIFICATIONS - Required for prayer time notifications
 *
 * Without these permissions, Adhan alarms may fail in:
 * - Doze mode
 * - Low-power modes
 * - App standby buckets
 * - Background execution restrictions
 */
object BatteryOptimizationHelper {

    /**
     * Check if exact alarm permission is granted (Android 12+)
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    /**
     * Check if app is exempted from battery optimizations
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    }

    /**
     * Get intent to request battery optimization exemption
     */
    fun getIgnoreBatteryOptimizationsIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        } else {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        }
    }

    /**
     * Get intent to request exact alarm permission (Android 12+)
     */
    fun getExactAlarmIntent(): Intent {
        return Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    }

    /**
     * Open system settings page for app
     */
    fun getAppSettingsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }

    /**
     * Check if all required permissions are granted
     */
    fun areAllRequiredPermissionsGranted(context: Context): Boolean {
        return canScheduleExactAlarms(context) && isIgnoringBatteryOptimizations(context)
    }
}