package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.repository.PrayerTimeRepository

class UpdatePrayerSettingsUseCase(
    private val prayerTimeRepository: PrayerTimeRepository
) {
    suspend operator fun invoke(settings: PrayerSettings) {
        prayerTimeRepository.updatePrayerSettings(settings)
    }
}