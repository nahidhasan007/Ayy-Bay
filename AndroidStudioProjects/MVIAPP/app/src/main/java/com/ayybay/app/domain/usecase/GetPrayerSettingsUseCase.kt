package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.repository.PrayerTimeRepository
import kotlinx.coroutines.flow.Flow

class GetPrayerSettingsUseCase(
    private val prayerTimeRepository: PrayerTimeRepository
) {
    operator fun invoke(): Flow<PrayerSettings> {
        return prayerTimeRepository.getPrayerSettings()
    }
}