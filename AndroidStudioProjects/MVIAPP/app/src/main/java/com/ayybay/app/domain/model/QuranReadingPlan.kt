package com.ayybay.app.domain.model

data class QuranReadingPlan(
    val durationDays: Int,
    val startDateKey: Long,
    val reminderHour: Int,
    val reminderMinute: Int,
    val lastMissedNotifiedDateKey: Long = 0L
)

/**
 * Snapshot of plan progress against the Surah-completion count. [expectedByToday] is the
 * cumulative Surah count the schedule expects by [daysElapsed] -- built by evenly
 * distributing all [totalSurahs] across [QuranReadingPlan.durationDays] days so the plan
 * always finishes exactly on schedule.
 */
data class QuranReadingPlanStatus(
    val plan: QuranReadingPlan,
    val totalSurahs: Int,
    val completedSurahs: Int,
    val daysElapsed: Int,
    val expectedByToday: Int
) {
    val daysRemaining: Int get() = (plan.durationDays - daysElapsed).coerceAtLeast(0)
    val isCompleted: Boolean get() = completedSurahs >= totalSurahs
    val surahsBehind: Int get() = (expectedByToday - completedSurahs).coerceAtLeast(0)
    val isBehind: Boolean get() = !isCompleted && surahsBehind > 0
    val targetEndDateKey: Long get() = plan.startDateKey + (plan.durationDays - 1) * DAY_MILLIS

    private companion object {
        const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}
