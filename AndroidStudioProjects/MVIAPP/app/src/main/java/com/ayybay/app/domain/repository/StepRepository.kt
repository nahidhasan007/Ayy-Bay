package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.StepDay
import kotlinx.coroutines.flow.Flow

interface StepRepository {

    val isSensorAvailable: Boolean

    val dailyGoal: Flow<Int>

    suspend fun setDailyGoal(goal: Int)

    /** Days with recorded steps in [fromKey, toKey); days without any steps are absent. */
    fun observeRange(fromKey: Long, toKey: Long): Flow<List<StepDay>>

    /** Reads the sensor once and credits any new steps to today. */
    suspend fun syncOnce()

    /** Keeps crediting steps as the sensor reports them; suspends until cancelled. */
    suspend fun trackLive()
}
