package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.SurahProgress
import com.ayybay.app.domain.repository.QuranProgressRepository
import kotlinx.coroutines.flow.Flow

class GetQuranProgressUseCase(
    private val quranProgressRepository: QuranProgressRepository
) {
    operator fun invoke(): Flow<List<SurahProgress>> = quranProgressRepository.getAllProgress()
}
