package com.ayybay.app.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ayybay.app.MainActivity
import com.ayybay.app.R
import com.ayybay.app.service.AdhanForegroundService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * BroadcastReceiver that handles prayer time alarms.
 *
 * CRITICAL: Starts foreground service for Adhan playback.
 * Without foreground service, audio playback would be blocked by:
 * - Android Doze mode
 * - App standby
 * - Background execution limits
 */
class AzanNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "azan_notifications"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: "Prayer Time"
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Create notification channel
        createNotificationChannel(context)

        // Show notification
        showNotification(context, prayerName, currentTime)

        // Start foreground service to play Adhan
        // CRITICAL: Must be foreground service for reliable audio playback
        AdhanForegroundService.startAdhan(
            context = context,
            prayerName = prayerName,
            durationSeconds = 90  // Play Adhan for 90 seconds
        )

        // Reschedule for next day (daily repeat)
        rescheduleForNextDay(context, prayerName, intent)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for prayer times"
                enableVibration(true)
                setShowBadge(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, prayerName: String, time: String) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("🕌 $prayerName Time")
            .setContentText("Playing Adhan at $time")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun rescheduleForNextDay(context: Context, prayerName: String, originalIntent: Intent) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager ?: return

        // Calculate time for next day (same time, +24 hours)
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(),
            originalIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Schedule for next day
        try {
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Fallback if exact alarm permission not granted
            alarmManager.setAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}