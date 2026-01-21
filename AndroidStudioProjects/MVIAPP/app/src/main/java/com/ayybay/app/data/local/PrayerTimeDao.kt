package com.ayybay.app.data.local

import androidx.room.*
import com.ayybay.app.data.local.entity.PrayerTimeEntity
import com.ayybay.app.data.local.entity.PrayerSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerTimeDao {

    @Query("SELECT * FROM prayer_settings WHERE id = 1")
    fun getPrayerSettings(): Flow<PrayerSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerSettings(settings: PrayerSettingsEntity)

    @Update
    suspend fun updatePrayerSettings(settings: PrayerSettingsEntity)

    @Query("SELECT * FROM prayer_times WHERE date = :date")
    fun getPrayerTimesForDate(date: Long): Flow<List<PrayerTimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(prayerTimes: List<PrayerTimeEntity>)

    @Query("UPDATE prayer_times SET isEnabled = :enabled WHERE prayerName = :prayerName AND date = :date")
    suspend fun togglePrayerNotification(prayerName: String, date: Long, enabled: Boolean)

    @Query("DELETE FROM prayer_times WHERE date < :beforeDate")
    suspend fun deleteOldPrayerTimes(beforeDate: Long)
}