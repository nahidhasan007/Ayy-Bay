package com.ayybay.app.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ayybay.app.data.local.QuranReadingPlanDao
import com.ayybay.app.data.local.entity.QuranReadingPlanEntity
import com.ayybay.app.domain.model.QuranReadingPlan
import com.ayybay.app.domain.repository.QuranReadingPlanRepository
import com.ayybay.app.receiver.QuranPlanReminderReceiver
import com.ayybay.app.util.RequestCodes
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date

class QuranReadingPlanRepositoryImpl(
    private val quranReadingPlanDao: QuranReadingPlanDao,
    private val context: Context
) : QuranReadingPlanRepository {

    override fun getPlan(): Flow<QuranReadingPlan?> =
        quranReadingPlanDao.getPlan().map { it?.toDomain() }

    override suspend fun getPlanOnce(): QuranReadingPlan? =
        quranReadingPlanDao.getPlanOnce()?.toDomain()

    override suspend fun startPlan(durationDays: Int, reminderHour: Int, reminderMinute: Int) {
        quranReadingPlanDao.upsertPlan(
            QuranReadingPlanEntity(
                durationDays = durationDays,
                startDateKey = Date().startOfDayMillis(),
                reminderHour = reminderHour,
                reminderMinute = reminderMinute
            )
        )
        scheduleReminder(reminderHour, reminderMinute)
    }

    override suspend fun updateReminderTime(reminderHour: Int, reminderMinute: Int) {
        val current = quranReadingPlanDao.getPlanOnce() ?: return
        quranReadingPlanDao.upsertPlan(current.copy(reminderHour = reminderHour, reminderMinute = reminderMinute))
        scheduleReminder(reminderHour, reminderMinute)
    }

    override suspend fun cancelPlan() {
        quranReadingPlanDao.clearPlan()
        cancelReminder()
    }

    override suspend fun markMissedNotified(dateKey: Long) {
        quranReadingPlanDao.updateLastNotifiedDate(dateKey)
    }

    override suspend fun rescheduleReminder() {
        val plan = quranReadingPlanDao.getPlanOnce() ?: return
        scheduleReminder(plan.reminderHour, plan.reminderMinute)
    }

    private fun scheduleReminder(hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAtMillis = nextTriggerMillis(hour, minute)
        val pendingIntent = reminderPendingIntent()
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun cancelReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RequestCodes.QURAN_PLAN_REMINDER,
            Intent(context, QuranPlanReminderReceiver::class.java).apply {
                action = QuranPlanReminderReceiver.ACTION_QURAN_PLAN_REMINDER
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    private fun reminderPendingIntent(): PendingIntent {
        val intent = Intent(context, QuranPlanReminderReceiver::class.java).apply {
            action = QuranPlanReminderReceiver.ACTION_QURAN_PLAN_REMINDER
        }
        return PendingIntent.getBroadcast(
            context,
            RequestCodes.QURAN_PLAN_REMINDER,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /** Next occurrence of [hour]:[minute] -- today if still ahead of now, else tomorrow. */
    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = System.currentTimeMillis()
        val candidate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (candidate.timeInMillis <= now) candidate.add(Calendar.DAY_OF_MONTH, 1)
        return candidate.timeInMillis
    }

    private fun QuranReadingPlanEntity.toDomain() = QuranReadingPlan(
        durationDays = durationDays,
        startDateKey = startDateKey,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute,
        lastMissedNotifiedDateKey = lastMissedNotifiedDateKey
    )
}
