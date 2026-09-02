package com.ayybay.app.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ayybay.app.data.local.PrayerTimeDao
import com.ayybay.app.data.mapper.PrayerTimeMapper
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.domain.repository.PrayerTimeRepository
import com.ayybay.app.receiver.AzanNotificationReceiver
import com.ayybay.app.util.RequestCodes
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date

class PrayerTimeRepositoryImpl(
    private val prayerTimeDao: PrayerTimeDao,
    private val context: Context
) : PrayerTimeRepository {

    override fun getPrayerTimesForDate(date: Date): Flow<List<PrayerTime>> {
        return prayerTimeDao.getPrayerTimesForDate(date.startOfDayMillis()).map { entities ->
            entities.map { PrayerTimeMapper.toDomain(it) }
        }
    }

    override fun getPrayerSettings(): Flow<PrayerSettings> {
        return prayerTimeDao.getPrayerSettings().map { entity ->
            PrayerTimeMapper.toDomainSettings(entity)
        }
    }

    override suspend fun updatePrayerSettings(settings: PrayerSettings) {
        prayerTimeDao.insertPrayerSettings(PrayerTimeMapper.toEntity(settings))
    }

    override suspend fun togglePrayerNotification(prayerName: PrayerName, enabled: Boolean) {
        // Per-prayer enabled flags live on the settings singleton, not the daily
        // prayer_times row -- the latter is recomputed (and its isEnabled reset to the
        // PrayerTime default of true) every time SchedulePrayerNotificationsUseCase runs,
        // which made this toggle silently forget itself by the next day.
        val current = PrayerTimeMapper.toDomainSettings(prayerTimeDao.getPrayerSettings().first())
        val updated = current.withPrayerEnabled(prayerName, enabled)
        prayerTimeDao.insertPrayerSettings(PrayerTimeMapper.toEntity(updated))
    }

    override suspend fun schedulePrayerNotification(prayerTime: PrayerTime) {
        if (!prayerTime.isEnabled) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AzanNotificationReceiver::class.java).apply {
            putExtra("prayer_name", prayerTime.prayerName.displayName)
            putExtra("prayer_time", prayerTime.time.time)
            action = "com.ayybay.app.AZAN_NOTIFICATION"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RequestCodes.forPrayer(prayerTime.prayerName.ordinal),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                prayerTime.time.time,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Handle security exception for exact alarm permission
            fallbackToInexactAlarm(alarmManager, prayerTime, pendingIntent)
        }
    }

    private fun fallbackToInexactAlarm(
        alarmManager: AlarmManager,
        prayerTime: PrayerTime,
        pendingIntent: PendingIntent
    ) {
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            prayerTime.time.time,
            pendingIntent
        )
    }

    override suspend fun cancelPrayerNotification(prayerName: PrayerName) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AzanNotificationReceiver::class.java).apply {
            action = "com.ayybay.app.AZAN_NOTIFICATION"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RequestCodes.forPrayer(prayerName.ordinal),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
        }
    }
}