package com.ayybay.app.presentation.mvi

import com.ayybay.app.data.local.DEFAULT_STEP_GOAL
import com.ayybay.app.domain.model.StepDay

data class StepUiState(
    val isSensorAvailable: Boolean = true,
    val hasPermission: Boolean = false,
    val dailyGoal: Int = DEFAULT_STEP_GOAL,
    val todayKey: Long = 0L,
    val todaySteps: Int = 0,
    /** Last 7 days ending today, oldest first; days with no data are present with 0 steps. */
    val week: List<StepDay> = emptyList(),
    val monthStartKey: Long = 0L,
    /** Days of the selected month that have recorded steps, oldest first. */
    val monthDays: List<StepDay> = emptyList(),
    val isCurrentMonth: Boolean = true
) {
    val weekTotalSteps: Long get() = week.sumOf { it.steps.toLong() }
    val monthTotalSteps: Long get() = monthDays.sumOf { it.steps.toLong() }
    val monthBestDay: StepDay? get() = monthDays.maxByOrNull { it.steps }?.takeIf { it.steps > 0 }
    val monthDaysGoalReached: Int get() = monthDays.count { it.steps >= dailyGoal }

    /** Average over days elapsed so far in the month, so untracked days count as zero. */
    fun monthDailyAverage(elapsedDays: Int): Long =
        if (elapsedDays > 0) monthTotalSteps / elapsedDays else 0L
}
