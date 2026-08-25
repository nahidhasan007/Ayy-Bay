package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.DayPrayerProgress
import com.ayybay.app.domain.repository.PrayerLogRepository
import kotlinx.coroutines.flow.Flow

class GetWeeklyPrayerProgressUseCase(
    private val prayerLogRepository: PrayerLogRepository
) {
    operator fun invoke(fromKey: Long): Flow<List<DayPrayerProgress>> =
        prayerLogRepository.getWeeklyProgress(fromKey)
}
