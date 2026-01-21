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
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ayybay.app.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ForegroundService responsible for playing Adhan audio.
 *
 * CRITICAL: Uses USAGE_ALARM audio attribute to ensure Adhan plays
 * even when phone is in Doze mode, silent mode, or on low priority.
 *
 * SIMPLIFIED APPROACH: Uses Ringtone which is guaranteed to work
 * on all Android versions (Android 7.0+ - minSdkVersion 24).
 */
class AdhanForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "adhan_playback_channel"
        private const val CHANNEL_NAME = "Adhan Playback"
        private const val NOTIFICATION_ID = 2001

        private const val EXTRA_PRAYER_NAME = "prayer_name"
        private const val EXTRA_ADHAN_DURATION = "adhan_duration"

        const val ACTION_START_ADHAN = "com.ayybay.app.action.START_ADHAN"
        const val ACTION_STOP_ADHAN = "com.ayybay.app.action.STOP_ADHAN"

        fun startAdhan(context: Context, prayerName: String, durationSeconds: Int = 15) {
            val intent = Intent(context, AdhanForegroundService::class.java).apply {
                action = ACTION_START_ADHAN
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_ADHAN_DURATION, durationSeconds)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopAdhan(context: Context) {
            val intent = Intent(context, AdhanForegroundService::class.java).apply {
                action = ACTION_STOP_ADHAN
            }
            context.startService(intent)
        }
    }

    private var currentPlayer: Ringtone? = null
    private val serviceScope = CoroutineScope(Job() + Dispatchers.Main)
    private var isPlaying = false
    private var playbackJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_START_ADHAN -> {
                val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer"
                val durationSeconds = intent.getIntExtra(EXTRA_ADHAN_DURATION, 15)
                startAdhanPlayback(prayerName, durationSeconds)
            }
            ACTION_STOP_ADHAN -> {
                stopAdhanPlayback()
            }
        }

        return START_NOT_STICKY
    }

    /**
     * Start playing Adhan audio
     * Tries to play custom azan2.mp3 first, falls back to system alarm sound
     */
    private fun startAdhanPlayback(prayerName: String, durationSeconds: Int) {
        if (isPlaying) return

        // Start foreground service with notification FIRST
        val notification = createAdhanNotification(prayerName)
        startForeground(NOTIFICATION_ID, notification)

        isPlaying = true
        playbackJob?.cancel()

        // Try to play custom Adhan file first
        val adhanUri = Uri.parse("android.resource://${packageName}/raw/azan2")
        val customAdhanPlayed = try {
            val ringtone = RingtoneManager.getRingtone(applicationContext, adhanUri)
            if (ringtone != null) {
                // Override audio attributes for custom Adhan
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                ringtone.audioAttributes = audioAttributes
                ringtone.play()
                currentPlayer = ringtone
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

        // Fallback to system alarm sound if custom file doesn't work
        if (!customAdhanPlayed) {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            try {
                val ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
                ringtone?.play()
                currentPlayer = ringtone
            } catch (e: Exception) {
                // Last resort: use notification sound
                val notificationUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(applicationContext, notificationUri)
                ringtone?.play()
                currentPlayer = ringtone
            }
        }

        // Stop after duration
        playbackJob = serviceScope.launch {
            delay((durationSeconds * 1000).toLong())
            stopAdhanPlayback()
        }
    }

    private fun stopAdhanPlayback() {
        currentPlayer?.stop()
        currentPlayer = null
        isPlaying = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Adhan playback notification"
                setShowBadge(false)
                setSound(null, null)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createAdhanNotification(prayerName: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Stop button intent
        val stopIntent = Intent(this, AdhanForegroundService::class.java).apply {
            action = ACTION_STOP_ADHAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🕌 $prayerName - Adhan Playing")
            .setContentText("Tap to open app")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Stop",
                stopPendingIntent
            )
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPlayer?.stop()
        currentPlayer = null
    }
}