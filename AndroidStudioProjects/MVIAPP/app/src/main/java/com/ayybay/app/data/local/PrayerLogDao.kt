package com.ayybay.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ayybay.app.data.local.entity.PrayerLogEntity
import kotlinx.coroutines.flow.Flow

data class DayPrayerCount(val dateKey: Long, val prayedCount: Int)

@Dao
interface PrayerLogDao {

    @Query("SELECT * FROM prayer_logs WHERE dateKey = :dateKey")
    fun getLogsForDate(dateKey: Long): Flow<List<PrayerLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(log: PrayerLogEntity)

    @Query(
        """
        SELECT dateKey, COUNT(*) as prayedCount FROM prayer_logs
        WHERE isPrayed = 1 AND dateKey >= :fromKey
        GROUP BY dateKey
        """
    )
    fun getWeeklyCounts(fromKey: Long): Flow<List<DayPrayerCount>>
}
