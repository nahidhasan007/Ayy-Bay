package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.PrayerLog
import com.ayybay.app.domain.repository.PrayerLogRepository
import kotlinx.coroutines.flow.Flow

class GetTodayPrayerLogUseCase(
    private val prayerLogRepository: PrayerLogRepository
) {
    operator fun invoke(dateKey: Long): Flow<List<PrayerLog>> = prayerLogRepository.getLogsForDate(dateKey)
}
