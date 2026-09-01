package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun getAllAlarms(): Flow<List<Alarm>>

    suspend fun getAlarmById(id: Long): Alarm?

    /** Persists the alarm and (re)schedules or cancels it with AlarmManager based on [Alarm.isEnabled]. */
    suspend fun upsertAlarm(alarm: Alarm): Long

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun setEnabled(id: Long, enabled: Boolean)

    /** Computes the next trigger time for [alarm] and schedules it with AlarmManager. */
    suspend fun scheduleAlarm(alarm: Alarm)

    suspend fun cancelAlarm(alarmId: Long)

    /** Re-schedules every enabled alarm; used after boot since AlarmManager entries don't survive a reboot. */
    suspend fun rescheduleAllEnabledAlarms()
}
