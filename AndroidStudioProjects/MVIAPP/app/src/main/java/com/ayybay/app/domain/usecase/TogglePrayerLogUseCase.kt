package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.repository.PrayerLogRepository

class TogglePrayerLogUseCase(
    private val prayerLogRepository: PrayerLogRepository
) {
    suspend operator fun invoke(dateKey: Long, prayerName: PrayerName, isPrayed: Boolean) {
        prayerLogRepository.setPrayed(dateKey, prayerName, isPrayed)
    }
}
