package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranReadingPlanRepository

class CancelQuranReadingPlanUseCase(
    private val quranReadingPlanRepository: QuranReadingPlanRepository
) {
    suspend operator fun invoke() = quranReadingPlanRepository.cancelPlan()
}
