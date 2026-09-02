package com.ayybay.app.domain.usecase

import com.ayybay.app.data.PrayerTimeCalculator
import com.ayybay.app.data.local.PrayerTimeDao
import com.ayybay.app.data.mapper.PrayerTimeMapper
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.repository.PrayerTimeRepository
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Date

class SchedulePrayerNotificationsUseCase(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val prayerTimeCalculator: PrayerTimeCalculator,
    private val prayerTimeDao: PrayerTimeDao
) {
    suspend operator fun invoke() {
        val settings = prayerTimeRepository.getPrayerSettings().first()
        val latitude = settings.locationLatitude ?: 23.8103
        val longitude = settings.locationLongitude ?: 90.4125

        val now = Date()
        val today = now
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time

        val todayPrayers = prayerTimeCalculator.calculatePrayerTimes(
            date = today,
            latitude = latitude,
            longitude = longitude,
            calculationMethod = settings.calculationMethod,
            madhab = settings.madhab
        )
        val tomorrowPrayers = prayerTimeCalculator.calculatePrayerTimes(
            date = tomorrow,
            latitude = latitude,
            longitude = longitude,
            calculationMethod = settings.calculationMethod,
            madhab = settings.madhab
        )

        // Persist today's prayer times for display, keyed by calendar day (not the exact
        // insert-time millisecond) so a later lookup with a fresh Date() still matches.
        val todayKey = Date(today.startOfDayMillis())
        prayerTimeDao.insertPrayerTimes(todayPrayers.map { PrayerTimeMapper.toEntity(it, todayKey) })

        // Schedule (or cancel) the next occurrence of each prayer based on the persisted
        // per-prayer enabled flag in settings -- previously this read isEnabled off the
        // freshly-calculated PrayerTime, which always defaults to true, so disabling a
        // prayer never actually stopped its alarm from firing.
        PrayerName.values().forEach { prayerName ->
            val todayPrayer = todayPrayers.find { it.prayerName == prayerName } ?: return@forEach
            val nextPrayer = if (todayPrayer.time.after(now)) todayPrayer
            else tomorrowPrayers.find { it.prayerName == prayerName } ?: return@forEach

            if (settings.isPrayerEnabled(prayerName)) {
                prayerTimeRepository.schedulePrayerNotification(nextPrayer)
            } else {
                prayerTimeRepository.cancelPrayerNotification(prayerName)
            }
        }
    }
}