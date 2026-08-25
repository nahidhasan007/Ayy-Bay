package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.QuranReadDay
import com.ayybay.app.domain.model.SurahProgress
import kotlinx.coroutines.flow.Flow

interface QuranProgressRepository {

    fun getAllProgress(): Flow<List<SurahProgress>>

    suspend fun setSurahCompleted(surahNumber: Int, completed: Boolean)

    suspend fun markSurahOpened(surahNumber: Int, dateKey: Long)

    fun getReadDays(fromKey: Long): Flow<List<QuranReadDay>>

    suspend fun getCurrentStreak(todayKey: Long, dayMillis: Long): Int
}
