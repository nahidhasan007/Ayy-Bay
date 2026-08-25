package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranProgressRepository

class GetQuranStreakUseCase(
    private val quranProgressRepository: QuranProgressRepository
) {
    suspend operator fun invoke(todayKey: Long, dayMillis: Long): Int =
        quranProgressRepository.getCurrentStreak(todayKey, dayMillis)
}
