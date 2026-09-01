package com.ayybay.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.ayybay.app.MainActivity
import com.ayybay.app.data.repository.AlarmRepositoryImpl.Companion.EXTRA_ALARM_ID
import com.ayybay.app.domain.model.Alarm

/**
 * Foreground service that rings until the user stops or snoozes it from [AlarmRingActivity]
 * (or from the notification's action buttons if the full-screen UI never surfaced).
 */
class AlarmRingingService : Service() {

    companion object {
        private const val CHANNEL_ID = "alarm_ringing_channel"
        private const val NOTIFICATION_ID = 3002

        private const val EXTRA_LABEL = "label"
        private const val EXTRA_HOUR = "hour"
        private const val EXTRA_MINUTE = "minute"
        private const val EXTRA_VIBRATE = "vibrate"

        const val ACTION_START = "com.ayybay.app.action.START_ALARM_RING"
        const val ACTION_STOP = "com.ayybay.app.action.STOP_ALARM_RING"

        fun start(context: Context, alarm: Alarm) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ALARM_ID, alarm.id)
                putExtra(EXTRA_LABEL, alarm.label)
                putExtra(EXTRA_HOUR, alarm.hour)
                putExtra(EXTRA_MINUTE, alarm.minute)
                putExtra(EXTRA_VIBRATE, alarm.vibrate)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.startService(Intent(context, AlarmRingingService::class.java).apply { action = ACTION_STOP })
        }
    }

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val label = intent.getStringExtra(EXTRA_LABEL).orEmpty()
                val hour = intent.getIntExtra(EXTRA_HOUR, 0)
                val minute = intent.getIntExtra(EXTRA_MINUTE, 0)
                val vibrate = intent.getBooleanExtra(EXTRA_VIBRATE, true)
                startRinging(label, hour, minute, vibrate)
            }
            ACTION_STOP -> stopRinging()
        }
        return START_NOT_STICKY
    }

    private fun startRinging(label: String, hour: Int, minute: Int, vibrate: Boolean) {
        val timeText = "%02d:%02d".format(hour, minute)
        startForeground(NOTIFICATION_ID, createNotification(label.ifBlank { "Alarm" }, timeText))

        val alarmUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        try {
            ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)?.apply {
                audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                isLooping = true
                play()
            }
        } catch (e: Exception) {
            // No ringtone available; vibration below still alerts the user.
        }

        if (vibrate) startVibrating()
    }

    private fun startVibrating() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 800, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopRinging() {
        ringtone?.stop()
        ringtone = null
        vibrator?.cancel()
        vibrator = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(com.ayybay.app.receiver.AlarmReceiver.NOTIFICATION_ID)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Ringing",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shown while an alarm is ringing"
                setShowBadge(false)
                setSound(null, null)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(label: String, timeText: String): Notification {
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, contentIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(label)
            .setContentText("Ringing — $timeText")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
        ringtone = null
        vibrator?.cancel()
        vibrator = null
    }
}
