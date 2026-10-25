package com.ayybay.app.util

import com.ayybay.app.domain.model.HourlyForecast
import com.ayybay.app.domain.model.RainAlert

/**
 * Pure decision logic for rain alerts, kept free of Android/network so it can be unit-tested.
 * Weather codes follow WMO: 51-67 drizzle/rain, 80-82 rain showers, 95-99 thunderstorm.
 */
object RainAlertLogic {

    /** How far ahead (hours, including the current one) an alert looks. */
    const val LOOKAHEAD_HOURS = 3

    /** After alerting for a rain spell, stay quiet for this long so one storm isn't re-announced every run. */
    const val COOLDOWN_SEC = 6 * 3600L

    /** Rain at or above this many mm in an hour is treated as heavy (WMO "heavy rain" starts at 7.6 mm/h). */
    const val HEAVY_RAIN_MM_PER_HOUR = 7.6

    private const val HOUR_SEC = 3600L

    fun isRainCode(code: Int): Boolean = code in 51..67 || code in 80..82 || code in 95..99

    fun isThunderstormCode(code: Int): Boolean = code in 95..99

    /**
     * First upcoming hour (from the hour containing [nowSec]) whose rain chance meets [thresholdPercent]
     * and which actually forecasts precipitation. A probability alone isn't enough: models report e.g.
     * 70% with 0.0 mm for trace showers, which isn't worth a notification.
     */
    fun findRainAlert(
        hourly: List<HourlyForecast>,
        nowSec: Long,
        thresholdPercent: Int,
        lookaheadHours: Int = LOOKAHEAD_HOURS
    ): RainAlert? {
        val windowStart = nowSec - nowSec % HOUR_SEC
        val windowEnd = windowStart + lookaheadHours * HOUR_SEC
        val hit = hourly
            .filter { it.timeSec in windowStart until windowEnd }
            .sortedBy { it.timeSec }
            .firstOrNull { hour ->
                hour.precipProbabilityPercent >= thresholdPercent &&
                    (hour.precipitationMm >= 0.1 || isRainCode(hour.weatherCode))
            } ?: return null
        return RainAlert(
            startSec = hit.timeSec,
            probabilityPercent = hit.precipProbabilityPercent,
            precipitationMm = hit.precipitationMm,
            isThunderstorm = isThunderstormCode(hit.weatherCode),
            isHeavy = hit.precipitationMm >= HEAVY_RAIN_MM_PER_HOUR || isThunderstormCode(hit.weatherCode)
        )
    }

    /** True unless we already alerted for this same spell of rain recently. */
    fun shouldNotify(alert: RainAlert, lastNotifiedStartSec: Long?): Boolean {
        if (lastNotifiedStartSec == null) return true
        return alert.startSec - lastNotifiedStartSec >= COOLDOWN_SEC
    }
}
