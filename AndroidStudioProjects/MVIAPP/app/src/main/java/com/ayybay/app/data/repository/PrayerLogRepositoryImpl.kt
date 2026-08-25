package com.ayybay.app.data.repository

import com.ayybay.app.data.local.PrayerLogDao
import com.ayybay.app.data.local.entity.PrayerLogEntity
import com.ayybay.app.domain.model.DayPrayerProgress
import com.ayybay.app.domain.model.PrayerLog
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.repository.PrayerLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrayerLogRepositoryImpl(
    private val prayerLogDao: PrayerLogDao
) : PrayerLogRepository {

    override fun getLogsForDate(dateKey: Long): Flow<List<PrayerLog>> =
        prayerLogDao.getLogsForDate(dateKey).map { list -> list.map { it.toDomain() } }

    override suspend fun setPrayed(dateKey: Long, prayerName: PrayerName, isPrayed: Boolean) {
        prayerLogDao.upsertLog(
            PrayerLogEntity(dateKey = dateKey, prayerName = prayerName.name, isPrayed = isPrayed)
        )
    }

    override fun getWeeklyProgress(fromKey: Long): Flow<List<DayPrayerProgress>> =
        prayerLogDao.getWeeklyCounts(fromKey).map { list ->
            list.map { DayPrayerProgress(dateKey = it.dateKey, prayedCount = it.prayedCount) }
        }

    private fun PrayerLogEntity.toDomain() = PrayerLog(
        dateKey = dateKey,
        prayerName = PrayerName.valueOf(prayerName),
        isPrayed = isPrayed
    )
}
