package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.QuranReadingPlan
import kotlinx.coroutines.flow.Flow

interface QuranReadingPlanRepository {

    fun getPlan(): Flow<QuranReadingPlan?>

    suspend fun getPlanOnce(): QuranReadingPlan?

    suspend fun startPlan(durationDays: Int, reminderHour: Int, reminderMinute: Int)

    suspend fun updateReminderTime(reminderHour: Int, reminderMinute: Int)

    suspend fun cancelPlan()

    suspend fun markMissedNotified(dateKey: Long)

    /** (Re)arms the daily reminder alarm for the active plan's reminder time. No-op if no plan is active. */
    suspend fun rescheduleReminder()
}
