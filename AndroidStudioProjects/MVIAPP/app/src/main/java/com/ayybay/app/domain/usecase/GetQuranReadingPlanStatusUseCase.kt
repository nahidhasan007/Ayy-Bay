package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranProgressRepository
import com.ayybay.app.domain.repository.QuranReadingPlanRepository
import com.ayybay.app.domain.model.QuranReadingPlanStatus
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date
import kotlin.math.ceil

private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

class GetQuranReadingPlanStatusUseCase(
    private val quranReadingPlanRepository: QuranReadingPlanRepository,
    private val quranProgressRepository: QuranProgressRepository
) {
    operator fun invoke(totalSurahs: Int): Flow<QuranReadingPlanStatus?> =
        combine(
            quranReadingPlanRepository.getPlan(),
            quranProgressRepository.getAllProgress()
        ) { plan, progress ->
            if (plan == null) return@combine null

            val completed = progress.count { it.isCompleted }
            val todayKey = Date().startOfDayMillis()
            val daysElapsed = ((todayKey - plan.startDateKey) / DAY_MILLIS + 1)
                .toInt()
                .coerceIn(1, plan.durationDays)
            val expectedByToday = ceil(totalSurahs * daysElapsed / plan.durationDays.toDouble())
                .toInt()
                .coerceAtMost(totalSurahs)

            QuranReadingPlanStatus(
                plan = plan,
                totalSurahs = totalSurahs,
                completedSurahs = completed,
                daysElapsed = daysElapsed,
                expectedByToday = expectedByToday
            )
        }
}
