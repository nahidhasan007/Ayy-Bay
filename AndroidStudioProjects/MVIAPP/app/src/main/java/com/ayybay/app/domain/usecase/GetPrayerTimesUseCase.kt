package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.domain.repository.PrayerTimeRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GetPrayerTimesUseCase(
    private val prayerTimeRepository: PrayerTimeRepository
) {
    operator fun invoke(date: Date): Flow<List<PrayerTime>> {
        return prayerTimeRepository.getPrayerTimesForDate(date)
    }
}