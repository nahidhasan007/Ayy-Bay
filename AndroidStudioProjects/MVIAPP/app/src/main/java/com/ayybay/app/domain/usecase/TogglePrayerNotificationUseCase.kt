package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.repository.PrayerTimeRepository

class TogglePrayerNotificationUseCase(
    private val prayerTimeRepository: PrayerTimeRepository
) {
    suspend operator fun invoke(prayerName: PrayerName, enabled: Boolean) {
        prayerTimeRepository.togglePrayerNotification(prayerName, enabled)
    }
}