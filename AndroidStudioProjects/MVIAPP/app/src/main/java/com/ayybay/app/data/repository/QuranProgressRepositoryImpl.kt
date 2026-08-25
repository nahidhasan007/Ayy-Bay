package com.ayybay.app.data.repository

import com.ayybay.app.data.local.QuranProgressDao
import com.ayybay.app.data.local.entity.QuranReadDayEntity
import com.ayybay.app.data.local.entity.SurahProgressEntity
import com.ayybay.app.domain.model.QuranReadDay
import com.ayybay.app.domain.model.SurahProgress
import com.ayybay.app.domain.repository.QuranProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuranProgressRepositoryImpl(
    private val quranProgressDao: QuranProgressDao
) : QuranProgressRepository {

    override fun getAllProgress(): Flow<List<SurahProgress>> =
        quranProgressDao.getAllProgress().map { list -> list.map { it.toDomain() } }

    override suspend fun setSurahCompleted(surahNumber: Int, completed: Boolean) {
        quranProgressDao.upsertProgress(
            SurahProgressEntity(
                surahNumber = surahNumber,
                isCompleted = completed,
                completedAt = if (completed) System.currentTimeMillis() else 0L
            )
        )
    }

    override suspend fun markSurahOpened(surahNumber: Int, dateKey: Long) {
        val existing = quranProgressDao.getReadDay(dateKey)
        quranProgressDao.upsertReadDay(
            QuranReadDayEntity(dateKey = dateKey, surahsOpened = (existing?.surahsOpened ?: 0) + 1)
        )
    }

    override fun getReadDays(fromKey: Long): Flow<List<QuranReadDay>> =
        quranProgressDao.getReadDays(fromKey).map { list -> list.map { QuranReadDay(it.dateKey, it.surahsOpened) } }

    override suspend fun getCurrentStreak(todayKey: Long, dayMillis: Long): Int {
        var streak = 0
        var cursor = todayKey
        while (true) {
            val day = quranProgressDao.getReadDay(cursor)
            if (day != null && day.surahsOpened > 0) {
                streak++
                cursor -= dayMillis
            } else {
                break
            }
        }
        return streak
    }

    private fun SurahProgressEntity.toDomain() = SurahProgress(
        surahNumber = surahNumber,
        isCompleted = isCompleted,
        completedAt = completedAt
    )
}
