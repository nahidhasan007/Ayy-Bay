package com.ayybay.app.data.repository

import com.ayybay.app.data.local.StepDao
import com.ayybay.app.data.local.StepPreferences
import com.ayybay.app.data.local.entity.StepDayEntity
import com.ayybay.app.data.sensor.StepSensor
import com.ayybay.app.domain.model.StepDay
import com.ayybay.app.domain.repository.StepRepository
import com.ayybay.app.util.StepMath
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date

private const val LIVE_WRITE_INTERVAL_MILLIS = 2_000L

class StepRepositoryImpl(
    private val stepDao: StepDao,
    private val stepSensor: StepSensor,
    private val stepPreferences: StepPreferences
) : StepRepository {

    // The worker and the screen can both feed readings in; the baseline must update atomically.
    private val recordLock = Mutex()

    override val isSensorAvailable: Boolean get() = stepSensor.isAvailable

    override val dailyGoal: Flow<Int> = stepPreferences.dailyGoal

    override suspend fun setDailyGoal(goal: Int) = stepPreferences.setDailyGoal(goal)

    override fun observeRange(fromKey: Long, toKey: Long): Flow<List<StepDay>> =
        stepDao.observeRange(fromKey, toKey).map { rows -> rows.map { StepDay(it.dateKey, it.steps) } }

    override suspend fun syncOnce() {
        stepSensor.readOnce()?.let { record(it) }
    }

    override suspend fun trackLive() {
        stepSensor.liveCounts().conflate().collect { counter ->
            record(counter)
            delay(LIVE_WRITE_INTERVAL_MILLIS)
        }
    }

    private suspend fun record(counter: Long) = recordLock.withLock {
        val delta = StepMath.delta(stepPreferences.lastCounter(), counter)
        stepPreferences.setLastCounter(counter)
        if (delta > 0) {
            val todayKey = Date().startOfDayMillis()
            stepDao.insertIfAbsent(StepDayEntity(todayKey, 0))
            stepDao.addSteps(todayKey, delta.toInt())
        }
    }
}
