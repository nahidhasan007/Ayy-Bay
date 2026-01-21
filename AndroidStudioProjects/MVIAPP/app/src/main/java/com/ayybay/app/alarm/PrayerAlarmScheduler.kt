package com.ayybay.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ayybay.app.receiver.AzanNotificationReceiver

/**
 * Scheduler for 5 daily Islamic prayer times.
 *
 * PRAYER TIMES (Fixed):
 * - Fajr:    5:00 AM   (05:00)
 * - Johr:    1:00 PM   (13:00)
 * - Asar:    5:00 PM   (17:00)
 * - Magrib:  6:10 PM   (18:10)
 * - Esha:    8:00 PM   (20:00)
 *
 * Features:
 * - Schedules alarms using AlarmManager.setExactAndAllowWhileIdle()
 * - Automatically reschedules to repeat daily
 * - Handles Android 12+ exact alarm permission requirements
 */
class PrayerAlarmScheduler(private val context: Context) {

    companion object {
        // Fixed prayer times
        private val PRAYER_TIMES = mapOf(
            "Fajr" to "05:00",    // 5:00 AM
            "Johr" to "13:00",    // 1:00 PM
            "Asar" to "17:00",    // 5:00 PM
            "Magrib" to "18:10",  // 6:10 PM
            "Esha" to "20:00"     // 8:00 PM
        )

        // Each prayer gets unique request code based on hash
        private fun getRequestCode(prayerName: String): Int = prayerName.hashCode()
    }

    /**
     * Schedule all 5 daily prayer alarms
     * Should be called once on app install/first launch
     */
    fun scheduleAllPrayers() {
        PRAYER_TIMES.forEach { (prayerName, timeStr) ->
            schedulePrayer(prayerName, timeStr)
        }
    }

    /**
     * Schedule a single prayer to occur at specified time and repeat daily
     */
    private fun schedulePrayer(prayerName: String, timeStr: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (hours, minutes) = timeStr.split(":").map { it.toInt() }

        // Calculate the next occurrence of this prayer time
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hours)
            set(java.util.Calendar.MINUTE, minutes)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Create intent for broadcast receiver
        val intent = Intent(context, AzanNotificationReceiver::class.java).apply {
            putExtra("prayer_name", prayerName)
            // Store the scheduled time for reference
            putExtra("scheduled_time", calendar.timeInMillis)
        }

        // Get or create pending intent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(prayerName),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Schedule the alarm
        try {
            // Try exact alarm first (Android 12+ requires permission)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Fallback if exact alarm permission not granted
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancel all prayer alarms
     */
    fun cancelAllPrayers() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        PRAYER_TIMES.keys.forEach { prayerName ->
            val intent = Intent(context, AzanNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(prayerName),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
            }
        }
    }
}