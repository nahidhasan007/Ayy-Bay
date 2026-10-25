package com.ayybay.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ayybay.app.MainActivity
import com.ayybay.app.data.local.LanguagePreferences
import com.ayybay.app.domain.model.AppNotification
import com.ayybay.app.domain.model.RainAlert
import com.ayybay.app.domain.model.WeatherSnapshot
import com.ayybay.app.domain.repository.WeatherRepository
import com.ayybay.app.domain.usecase.AddNotificationUseCase
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.trOf
import com.ayybay.app.presentation.util.formatHourLabel
import com.ayybay.app.presentation.util.localizeDigits
import com.ayybay.app.util.RainAlertLogic
import com.ayybay.app.util.RequestCodes
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

/**
 * Periodically fetches the forecast and notifies when rain (or a thunderstorm) is likely within the
 * next few hours. Only runs while the user has rain alerts switched on; the decision logic lives in
 * [RainAlertLogic]. Uses the saved/selected weather location -- never background GPS.
 */
class RainAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.get()
        val repository = koin.get<WeatherRepository>()

        val settings = repository.alertSettings.first()
        if (!settings.enabled) return Result.success()

        val snapshot = repository.refresh().getOrElse {
            // Offline or API hiccup: retry shortly a couple of times, otherwise wait for the next period.
            return if (runAttemptCount < MAX_ATTEMPTS) Result.retry() else Result.success()
        }

        val alert = RainAlertLogic.findRainAlert(
            hourly = snapshot.hourly,
            nowSec = System.currentTimeMillis() / 1000,
            thresholdPercent = settings.thresholdPercent
        ) ?: return Result.success()

        if (!RainAlertLogic.shouldNotify(alert, repository.lastRainAlertStartSec())) return Result.success()

        val language = koin.get<LanguagePreferences>().language.first()
        notify(snapshot, alert, language, koin.get())
        repository.setLastRainAlertStartSec(alert.startSec)
        return Result.success()
    }

    private suspend fun notify(
        snapshot: WeatherSnapshot,
        alert: RainAlert,
        language: AppLanguage,
        addNotification: AddNotificationUseCase
    ) {
        val place = snapshot.placeName.ifBlank { null }
        val timeEn = formatHourLabel(alert.startSec, snapshot.utcOffsetSeconds, AppLanguage.EN)
        val timeBn = formatHourLabel(alert.startSec, snapshot.utcOffsetSeconds, AppLanguage.BN)
        val chance = alert.probabilityPercent

        val titleEn: String
        val titleBn: String
        when {
            alert.isThunderstorm -> {
                titleEn = "⛈️ Thunderstorm expected"
                titleBn = "⛈️ বজ্রসহ বৃষ্টির সম্ভাবনা"
            }
            alert.isHeavy -> {
                titleEn = "🌧️ Heavy rain expected"
                titleBn = "🌧️ ভারী বৃষ্টির সম্ভাবনা"
            }
            else -> {
                titleEn = "🌧️ Rain expected soon"
                titleBn = "🌧️ শীঘ্রই বৃষ্টির সম্ভাবনা"
            }
        }
        val bodyEn = "$chance% chance of rain around $timeEn" + (place?.let { " in $it" } ?: "") + ". Take an umbrella!"
        val bodyBn = (place?.let { "$it-এ " } ?: "") + "$timeBn নাগাদ বৃষ্টির সম্ভাবনা ${localizeDigits(chance.toString(), AppLanguage.BN)}%। ছাতা সঙ্গে রাখুন!"

        postNotification(trOf(language, titleEn, titleBn), trOf(language, bodyEn, bodyBn))
        addNotification(
            AppNotification(
                type = "rain_alert",
                titleEn = titleEn,
                titleBn = titleBn,
                bodyEn = bodyEn,
                bodyBn = bodyBn,
                timestamp = System.currentTimeMillis(),
                deepLinkRoute = "weather"
            )
        )
    }

    private fun postNotification(title: String, body: String) {
        val context = applicationContext
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Weather & Rain Alerts", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Heads-up when rain or a thunderstorm is expected nearby"
                }
            )
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(RequestCodes.RAIN_ALERT, notification)
    }

    companion object {
        private const val WORK_NAME = "rain_alert_check"
        private const val CHANNEL_ID = "weather_rain_alerts"
        private const val MAX_ATTEMPTS = 3

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RainAlertWorker>(30, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
