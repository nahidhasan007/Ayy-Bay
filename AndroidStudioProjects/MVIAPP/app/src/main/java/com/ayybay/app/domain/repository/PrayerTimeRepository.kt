package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.domain.model.PrayerSettings
import kotlinx.coroutines.flow.Flow

interface PrayerTimeRepository {
    fun getPrayerTimesForDate(date: java.util.Date): Flow<List<PrayerTime>>

    fun getPrayerSettings(): Flow<PrayerSettings>

    suspend fun updatePrayerSettings(settings: PrayerSettings)

    suspend fun togglePrayerNotification(
        prayerName: com.ayybay.app.domain.model.PrayerName,
        enabled: Boolean
    )

    suspend fun schedulePrayerNotification(prayerTime: PrayerTime)

    suspend fun cancelPrayerNotification(prayerName: com.ayybay.app.domain.model.PrayerName)
}