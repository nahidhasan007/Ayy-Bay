package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranProgressRepository

class MarkSurahReadUseCase(
    private val quranProgressRepository: QuranProgressRepository
) {
    suspend operator fun invoke(surahNumber: Int, dateKey: Long) {
        quranProgressRepository.markSurahOpened(surahNumber, dateKey)
    }
}
