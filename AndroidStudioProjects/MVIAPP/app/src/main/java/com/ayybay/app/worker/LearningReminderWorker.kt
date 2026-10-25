package com.ayybay.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ayybay.app.MainActivity
import com.ayybay.app.data.local.LanguagePreferences
import com.ayybay.app.data.local.LearningContent
import com.ayybay.app.data.local.LearningPreferences
import com.ayybay.app.domain.model.AppNotification
import com.ayybay.app.domain.model.LearningCategory
import com.ayybay.app.domain.usecase.AddNotificationUseCase
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.trOf
import com.ayybay.app.presentation.util.localizeDigits
import com.ayybay.app.util.LearningStreak
import com.ayybay.app.util.RequestCodes
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Nudges the user once a day at their chosen hour -- only if they haven't learned anything yet that
 * day. Scheduled as a 24h periodic request whose first run is delayed to the chosen hour; WorkManager
 * advances periodic work by whole periods, so later runs stay at that hour of day.
 */
class LearningReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.get()
        val progress = koin.get<LearningPreferences>().progressOnce()
        if (!progress.reminderEnabled) return Result.success()

        val today = LearningStreak.today()
        if (today !in progress.activeDays) {
            val language = koin.get<LanguagePreferences>().language.first()
            notify(progress.activeDays, today, language, koin.get())
        }

        return Result.success()
    }

    private suspend fun notify(activeDays: Set<Long>, today: Long, language: AppLanguage, addNotification: AddNotificationUseCase) {
        val streak = LearningStreak.current(activeDays, today)
        val word = LearningContent.itemFor(LearningCategory.VOCABULARY, today).titleEn
        val streakBn = localizeDigits(streak.toString(), AppLanguage.BN)

        val titleEn = "📚 Today's learning is ready"
        val titleBn = "📚 আজকের শিক্ষা প্রস্তুত"
        val bodyEn = if (streak > 0) "Keep your $streak-day streak alive! Today's word: $word." else "A verse, a word and a fact are waiting. Today's word: $word."
        val bodyBn = if (streak > 0) "আপনার $streakBn দিনের ধারা ধরে রাখুন! আজকের শব্দ: $word।" else "একটি আয়াত/হাদিস, একটি শব্দ ও একটি তথ্য অপেক্ষা করছে। আজকের শব্দ: $word।"

        postNotification(trOf(language, titleEn, titleBn), trOf(language, bodyEn, bodyBn))
        addNotification(
            AppNotification(
                type = "daily_learning",
                titleEn = titleEn,
                titleBn = titleBn,
                bodyEn = bodyEn,
                bodyBn = bodyBn,
                timestamp = System.currentTimeMillis(),
                deepLinkRoute = "daily_learning"
            )
        )
    }

    private fun postNotification(title: String, body: String) {
        val context = applicationContext
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Daily Learning", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "A daily reminder to read today's lesson and take the quiz"
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
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(RequestCodes.LEARNING_REMINDER, notification)
    }

    companion object {
        private const val WORK_NAME = "learning_reminder"
        private const val CHANNEL_ID = "daily_learning_reminders"

        /** Schedules the daily reminder to first fire at the next [hour]:00, replacing any earlier schedule. */
        fun schedule(context: Context, hour: Int) {
            val now = LocalDateTime.now()
            var next = now.toLocalDate().atTime(hour, 0)
            if (!next.isAfter(now)) next = next.plusDays(1)
            val request = PeriodicWorkRequestBuilder<LearningReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(Duration.between(now, next).toMillis(), TimeUnit.MILLISECONDS)
                .build()
            // Cancel first so the new initial delay takes effect when the hour changes; an in-place
            // update of existing periodic work would keep its old next-run time.
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME)
            workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
