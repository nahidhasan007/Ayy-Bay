package com.ayybay.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ayybay.app.data.local.entity.QuranReadDayEntity
import com.ayybay.app.data.local.entity.SurahProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranProgressDao {

    @Query("SELECT * FROM surah_progress")
    fun getAllProgress(): Flow<List<SurahProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: SurahProgressEntity)

    @Query("SELECT * FROM quran_read_days WHERE dateKey >= :fromKey ORDER BY dateKey ASC")
    fun getReadDays(fromKey: Long): Flow<List<QuranReadDayEntity>>

    @Query("SELECT * FROM quran_read_days WHERE dateKey = :dateKey")
    suspend fun getReadDay(dateKey: Long): QuranReadDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReadDay(day: QuranReadDayEntity)
}
