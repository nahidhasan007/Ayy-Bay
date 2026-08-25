package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.DayPrayerProgress
import com.ayybay.app.domain.model.PrayerLog
import com.ayybay.app.domain.model.PrayerName
import kotlinx.coroutines.flow.Flow

interface PrayerLogRepository {

    fun getLogsForDate(dateKey: Long): Flow<List<PrayerLog>>

    suspend fun setPrayed(dateKey: Long, prayerName: PrayerName, isPrayed: Boolean)

    fun getWeeklyProgress(fromKey: Long): Flow<List<DayPrayerProgress>>
}
