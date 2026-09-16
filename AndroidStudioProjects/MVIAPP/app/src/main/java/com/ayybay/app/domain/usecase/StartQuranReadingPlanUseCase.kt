package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranReadingPlanRepository

class StartQuranReadingPlanUseCase(
    private val quranReadingPlanRepository: QuranReadingPlanRepository
) {
    suspend operator fun invoke(durationDays: Int, reminderHour: Int, reminderMinute: Int) {
        require(durationDays > 0) { "durationDays must be positive" }
        quranReadingPlanRepository.startPlan(durationDays, reminderHour, reminderMinute)
    }
}
