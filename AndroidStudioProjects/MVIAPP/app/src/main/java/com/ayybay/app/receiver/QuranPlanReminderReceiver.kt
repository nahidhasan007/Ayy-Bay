package com.ayybay.app.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ayybay.app.MainActivity
import com.ayybay.app.data.local.LanguagePreferences
import com.ayybay.app.data.local.QuranSurahData
import com.ayybay.app.domain.model.AppNotification
import com.ayybay.app.domain.repository.QuranProgressRepository
import com.ayybay.app.domain.repository.QuranReadingPlanRepository
import com.ayybay.app.domain.usecase.AddNotificationUseCase
import com.ayybay.app.presentation.language.trOf
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date
import kotlin.math.ceil

/**
 * Fired daily by AlarmManager at the user's chosen reminder time for the active Quran
 * reading plan. Compares actual vs. expected Surah completion: notifies once if behind
 * schedule, congratulates and clears the plan once all surahs are done, otherwise re-arms
 * itself for tomorrow (self-rescheduling, same pattern as [AzanNotificationReceiver]).
 */
class QuranPlanReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val quranReadingPlanRepository: QuranReadingPlanRepository by inject()
    private val quranProgressRepository: QuranProgressRepository by inject()
    private val addNotificationUseCase: AddNotificationUseCase by inject()
    private val languagePreferences: LanguagePreferences by inject()

    companion object {
        const val NOTIFICATION_ID = 1500
        const val CHANNEL_ID = "quran_plan_notifications"
        const val ACTION_QURAN_PLAN_REMINDER = "com.ayybay.app.QURAN_PLAN_REMINDER"
        private const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        scope.launch {
            try {
                val plan = quranReadingPlanRepository.getPlanOnce() ?: return@launch
                val totalSurahs = QuranSurahData.surahs().size
                val completed = quranProgressRepository.getAllProgress().first().count { it.isCompleted }

                if (completed >= totalSurahs) {
                    notifyCompletion(context, totalSurahs)
                    quranReadingPlanRepository.cancelPlan()
                    return@launch
                }

                val todayKey = Date().startOfDayMillis()
                val daysElapsed = ((todayKey - plan.startDateKey) / DAY_MILLIS + 1)
                    .toInt()
                    .coerceIn(1, plan.durationDays)
                val expectedByToday = ceil(totalSurahs * daysElapsed / plan.durationDays.toDouble())
                    .toInt()
                    .coerceAtMost(totalSurahs)

                if (completed < expectedByToday && plan.lastMissedNotifiedDateKey != todayKey) {
                    notifyBehindSchedule(context, completed, expectedByToday, totalSurahs)
                    quranReadingPlanRepository.markMissedNotified(todayKey)
                }

                quranReadingPlanRepository.rescheduleReminder()
            } catch (e: Exception) {
                // Never let a single day's check crash the process or block future reminders.
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun notifyBehindSchedule(context: Context, completed: Int, expected: Int, total: Int) {
        val behind = expected - completed
        val language = languagePreferences.language.first()
        val titleEn = "📖 You're behind on your Quran plan"
        val titleBn = "📖 আপনি কুরআন পরিকল্পনায় পিছিয়ে আছেন"
        val bodyEn = "You've completed $completed of $total surahs -- $behind behind today's target. Catch up to stay on track!"
        val bodyBn = "আপনি $total টির মধ্যে $completed টি সূরা সম্পন্ন করেছেন -- আজকের লক্ষ্য থেকে $behind টি পিছিয়ে আছেন। এগিয়ে যেতে পড়া চালিয়ে যান!"
        createNotificationChannel(context)
        postNotification(context, trOf(language, titleEn, titleBn), trOf(language, bodyEn, bodyBn))
        addNotificationUseCase(
            AppNotification(
                type = "quran_plan_behind",
                titleEn = titleEn,
                titleBn = titleBn,
                bodyEn = bodyEn,
                bodyBn = bodyBn,
                timestamp = System.currentTimeMillis(),
                deepLinkRoute = "quran_reading_plan"
            )
        )
    }

    private suspend fun notifyCompletion(context: Context, total: Int) {
        val language = languagePreferences.language.first()
        val titleEn = "🎉 Quran Completed!"
        val titleBn = "🎉 কুরআন সম্পন্ন হয়েছে!"
        val bodyEn = "Khatam Mubarak! You finished all $total surahs."
        val bodyBn = "খতম মোবারক! আপনি সবগুলো ($total) সূরা সম্পন্ন করেছেন।"
        createNotificationChannel(context)
        postNotification(context, trOf(language, titleEn, titleBn), trOf(language, bodyEn, bodyBn))
        addNotificationUseCase(
            AppNotification(
                type = "quran_plan_completed",
                titleEn = titleEn,
                titleBn = titleBn,
                bodyEn = bodyEn,
                bodyBn = bodyBn,
                timestamp = System.currentTimeMillis(),
                deepLinkRoute = "quran_reading_plan"
            )
        )
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quran Reading Plan",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders about your Quran reading plan progress"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun postNotification(context: Context, title: String, body: String) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
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
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
