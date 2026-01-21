package com.ayybay.app.domain.usecase

import com.ayybay.app.data.PrayerTimeCalculator
import com.ayybay.app.data.local.PrayerTimeDao
import com.ayybay.app.data.mapper.PrayerTimeMapper
import com.ayybay.app.domain.model.CalculationMethod
import com.ayybay.app.domain.model.Madhab
import com.ayybay.app.domain.repository.PrayerTimeRepository
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.flow.first

class SchedulePrayerNotificationsUseCase(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val prayerTimeCalculator: PrayerTimeCalculator,
    private val prayerTimeDao: PrayerTimeDao
) {
    suspend operator fun invoke() {
        val calendar = Calendar.getInstance()

        // Calculate and schedule for today and next 6 days
        for (i in 0..6) {
            val date = Date(calendar.timeInMillis + (i * 24 * 60 * 60 * 1000))
            schedulePrayersForDate(date)
        }
    }

    private suspend fun schedulePrayersForDate(date: Date) {
        // Get prayer settings to get location
        val settings = prayerTimeRepository.getPrayerSettings().first()

        // Use default location if not set (e.g., Makkah)
        val latitude = settings.locationLatitude ?: 21.4225
        val longitude = settings.locationLongitude ?: 39.8262
        val calculationMethod = settings.calculationMethod ?: CalculationMethod.MWL
        val madhab = settings.madhab ?: Madhab.SHAFI

        // Calculate prayer times
        val prayerTimes = prayerTimeCalculator.calculatePrayerTimes(
            latitude = latitude,
            longitude = longitude,
            date = date,
            calculationMethod = calculationMethod,
            madhab = madhab
        )

        // Save prayer times to database
        val prayerTimeEntities = prayerTimes.map {
        PrayerTimeMapper.toEntity(it, date)
        }
        prayerTimeDao.insertPrayerTimes(prayerTimeEntities)

        // Schedule notifications
        prayerTimes.forEach { prayerTime ->
            prayerTimeRepository.schedulePrayerNotification(prayerTime)
        }
    }
}