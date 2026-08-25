package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranProgressRepository

class ToggleSurahCompleteUseCase(
    private val quranProgressRepository: QuranProgressRepository
) {
    suspend operator fun invoke(surahNumber: Int, completed: Boolean) {
        quranProgressRepository.setSurahCompleted(surahNumber, completed)
    }
}
