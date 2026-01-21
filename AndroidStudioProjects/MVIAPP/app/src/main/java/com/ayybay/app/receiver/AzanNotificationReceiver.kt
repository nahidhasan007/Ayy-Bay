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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AzanNotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "azan_channel"
        private const val CHANNEL_NAME = "Prayer Times"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val prayerName = intent.getStringExtra("prayer_name") ?: return
        val prayerTime = intent.getLongExtra("prayer_time", 0)

        createNotificationChannel(context)
        showNotification(context, prayerName, prayerTime)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
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

    private fun showNotification(context: Context, prayerName: String, prayerTime: Long) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = timeFormat.format(Date(prayerTime))

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🕌 $prayerName - Time for Prayer")
            .setContentText("It's time for $prayerName prayer at $formattedTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationIdForPrayer(prayerName)?.let { id ->
            notificationManager.notify(id, notification)
        }
    }

    private fun notificationIdForPrayer(prayerName: String): Int? {
        return when (prayerName) {
            "Fajr" -> NOTIFICATION_ID_BASE + 1
            "Dhuhr" -> NOTIFICATION_ID_BASE + 2
            "Asr" -> NOTIFICATION_ID_BASE + 3
            "Maghrib" -> NOTIFICATION_ID_BASE + 4
            "Isha" -> NOTIFICATION_ID_BASE + 5
            else -> null
        }
    }
}