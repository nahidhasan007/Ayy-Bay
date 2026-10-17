package com.ayybay.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ayybay.app.data.local.entity.StepDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    @Query("SELECT * FROM step_days WHERE dateKey >= :fromKey AND dateKey < :toKey ORDER BY dateKey")
    fun observeRange(fromKey: Long, toKey: Long): Flow<List<StepDayEntity>>

    /** Creates the day's row at 0 steps if it doesn't exist yet; a no-op otherwise. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(day: StepDayEntity)

    @Query("UPDATE step_days SET steps = steps + :delta WHERE dateKey = :dateKey")
    suspend fun addSteps(dateKey: Long, delta: Int)
}
