package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranReadingPlanRepository

class UpdateReadingPlanReminderTimeUseCase(
    private val quranReadingPlanRepository: QuranReadingPlanRepository
) {
    suspend operator fun invoke(hour: Int, minute: Int) =
        quranReadingPlanRepository.updateReminderTime(hour, minute)
}
