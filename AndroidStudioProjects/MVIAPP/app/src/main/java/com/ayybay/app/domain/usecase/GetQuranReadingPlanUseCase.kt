package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.QuranReadingPlan
import com.ayybay.app.domain.repository.QuranReadingPlanRepository
import kotlinx.coroutines.flow.Flow

class GetQuranReadingPlanUseCase(
    private val quranReadingPlanRepository: QuranReadingPlanRepository
) {
    operator fun invoke(): Flow<QuranReadingPlan?> = quranReadingPlanRepository.getPlan()
}
