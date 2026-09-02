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
import com.ayybay.app.data.local.LanguagePreferences
import com.ayybay.app.domain.model.AppNotification
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.usecase.AddNotificationUseCase
import com.ayybay.app.domain.usecase.SchedulePrayerNotificationsUseCase
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.bnLabel
import com.ayybay.app.presentation.language.trOf
import com.ayybay.app.service.AdhanForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fired by AlarmManager at each prayer's exact time. Plays the Adhan, posts a notification,
 * and then re-syncs the FULL prayer schedule from the user's real settings (location,
 * calculation method, madhab, per-prayer enabled flags) via [SchedulePrayerNotificationsUseCase]
 * -- rather than recomputing just this one prayer from hardcoded Dhaka/Karachi/Hanafi, which is
 * how the fired alarm used to silently drift from what the app displayed and from what the user
 * had configured. This also makes the schedule self-healing: every prayer that fires re-syncs it.
 */
class AzanNotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val schedulePrayerNotificationsUseCase: SchedulePrayerNotificationsUseCase by inject()
    private val languagePreferences: LanguagePreferences by inject()
    private val addNotificationUseCase: AddNotificationUseCase by inject()

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "azan_notifications"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: "Prayer Time"
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        AdhanForegroundService.startAdhan(
            context = context,
            prayerName = prayerName,
            durationSeconds = 90
        )

        val pendingResult = goAsync()
        scope.launch {
            try {
                val language = languagePreferences.language.first()
                createNotificationChannel(context)
                showNotification(context, prayerName, currentTime, language)
                recordNotification(prayerName, currentTime)
                schedulePrayerNotificationsUseCase()
            } finally {
                pendingResult.finish()
            }
        }
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

    private fun showNotification(context: Context, prayerName: String, time: String, language: AppLanguage) {
        val prayerEnum = PrayerName.values().find {
            it.displayName.equals(prayerName, ignoreCase = true) || it.name.equals(prayerName, ignoreCase = true)
        }
        val localizedPrayerName = prayerEnum?.let { trOf(language, it.displayName, it.bnLabel()) } ?: prayerName

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = trOf(language, "🕌 $localizedPrayerName Time", "🕌 $localizedPrayerName এর সময়")
        val body = trOf(language, "Adhan playing at $time", "আজান বাজছে $time এ")
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /** Persists a bilingual record for the in-app notification center (independent of the system notification above, which resolves to a single language at post time). */
    private suspend fun recordNotification(prayerName: String, time: String) {
        val prayerEnum = PrayerName.values().find {
            it.displayName.equals(prayerName, ignoreCase = true) || it.name.equals(prayerName, ignoreCase = true)
        }
        val nameEn = prayerEnum?.displayName ?: prayerName
        val nameBn = prayerEnum?.bnLabel() ?: prayerName
        addNotificationUseCase(
            AppNotification(
                type = "adhan",
                titleEn = "🕌 $nameEn Time",
                titleBn = "🕌 $nameBn এর সময়",
                bodyEn = "Adhan played at $time",
                bodyBn = "আজান বাজানো হয়েছে $time এ",
                timestamp = System.currentTimeMillis(),
                deepLinkRoute = "prayer_times"
            )
        )
    }
}
