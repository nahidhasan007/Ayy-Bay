package com.ayybay.app.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ayybay.app.data.local.AlarmDao
import com.ayybay.app.data.local.entity.AlarmEntity
import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.domain.repository.AlarmRepository
import com.ayybay.app.receiver.AlarmReceiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao,
    private val context: Context
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> =
        alarmDao.getAllAlarms().map { it.map { e -> e.toDomain() } }

    override suspend fun getAlarmById(id: Long): Alarm? =
        alarmDao.getAlarmById(id)?.toDomain()

    override suspend fun upsertAlarm(alarm: Alarm): Long {
        val entity = alarm.toEntity().copy(
            createdAt = if (alarm.id == 0L) System.currentTimeMillis() else alarm.createdAt
        )
        val id = alarmDao.upsertAlarm(entity)
        val saved = alarm.copy(id = id)
        if (saved.isEnabled) scheduleAlarm(saved) else cancelAlarm(saved.id)
        return id
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        cancelAlarm(alarm.id)
        alarmDao.deleteAlarm(alarm.toEntity())
    }

    override suspend fun setEnabled(id: Long, enabled: Boolean) {
        alarmDao.setEnabled(id, enabled)
        if (enabled) {
            alarmDao.getAlarmById(id)?.toDomain()?.let { scheduleAlarm(it) }
        } else {
            cancelAlarm(id)
        }
    }

    override suspend fun scheduleAlarm(alarm: Alarm) {
        val triggerAtMillis = nextTriggerMillis(alarm) ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = mainPendingIntent(alarm.id)

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    override suspend fun cancelAlarm(alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply { action = ACTION_ALARM_RING }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    override suspend fun rescheduleAllEnabledAlarms() {
        alarmDao.getEnabledAlarms().forEach { scheduleAlarm(it.toDomain()) }
    }

    private fun mainPendingIntent(alarmId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_RING
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /** Next strictly-future trigger time for [alarm], or null if it has no valid schedule. */
    private fun nextTriggerMillis(alarm: Alarm): Long? {
        val now = System.currentTimeMillis()
        val base = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alarm.repeatDays.isEmpty()) {
            if (base.timeInMillis <= now) base.add(Calendar.DAY_OF_MONTH, 1)
            return base.timeInMillis
        }

        for (dayOffset in 0..7) {
            val candidate = (base.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, dayOffset) }
            if (candidate.timeInMillis > now && alarm.repeatDays.contains(candidate.get(Calendar.DAY_OF_WEEK))) {
                return candidate.timeInMillis
            }
        }
        return null
    }

    private fun AlarmEntity.toDomain() = Alarm(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        isEnabled = isEnabled,
        repeatDays = if (repeatDays.isBlank()) emptySet() else repeatDays.split(",").map { it.toInt() }.toSet(),
        vibrate = vibrate,
        createdAt = createdAt
    )

    private fun Alarm.toEntity() = AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        isEnabled = isEnabled,
        repeatDays = repeatDays.sorted().joinToString(","),
        vibrate = vibrate,
        createdAt = createdAt
    )

    companion object {
        const val ACTION_ALARM_RING = "com.ayybay.app.ALARM_RING"
        const val EXTRA_ALARM_ID = "alarm_id"
    }
}
