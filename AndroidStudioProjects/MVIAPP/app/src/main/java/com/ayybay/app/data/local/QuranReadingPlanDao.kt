package com.ayybay.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ayybay.app.data.local.entity.QuranReadingPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranReadingPlanDao {

    @Query("SELECT * FROM quran_reading_plan WHERE id = 1")
    fun getPlan(): Flow<QuranReadingPlanEntity?>

    @Query("SELECT * FROM quran_reading_plan WHERE id = 1")
    suspend fun getPlanOnce(): QuranReadingPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlan(plan: QuranReadingPlanEntity)

    @Query("DELETE FROM quran_reading_plan")
    suspend fun clearPlan()

    @Query("UPDATE quran_reading_plan SET lastMissedNotifiedDateKey = :dateKey WHERE id = 1")
    suspend fun updateLastNotifiedDate(dateKey: Long)
}
