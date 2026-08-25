package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.QuranReadDay
import com.ayybay.app.domain.repository.QuranProgressRepository
import kotlinx.coroutines.flow.Flow

class GetQuranWeeklyReadingUseCase(
    private val quranProgressRepository: QuranProgressRepository
) {
    operator fun invoke(fromKey: Long): Flow<List<QuranReadDay>> = quranProgressRepository.getReadDays(fromKey)
}
