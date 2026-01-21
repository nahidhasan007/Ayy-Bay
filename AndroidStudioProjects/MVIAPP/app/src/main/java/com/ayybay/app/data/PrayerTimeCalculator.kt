package com.ayybay.app.data

import com.ayybay.app.domain.model.*
import java.util.Calendar
import java.util.Date
import kotlin.math.*

/**
 * PrayerTimeCalculator calculates prayer times based on location and calculation method.
 *
 * BANGLADESH CONFIGURATION:
 * - Default Method: Karachi (最适合孟加拉国)
 * - Default Madhab: Hanafi (孟加拉国大多数穆斯林遵循)
 * - Coordinates: Dhaka (23.8103° N, 90.4125° E)
 * - Timezone: GMT+6
 */
class PrayerTimeCalculator {

    private val bangladeshTimezoneOffset = 6.0 // GMT+6 hours for Bangladesh

    fun calculatePrayerTimes(
        date: Date,
        latitude: Double,
        longitude: Double,
        calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
        madhab: Madhab = Madhab.HANAFI
    ): List<PrayerTime> {

        val calendar = Calendar.getInstance().apply { time = date }
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        // Calculate equation of time and declination
        val (eqOfTime, declination) = sunPosition(dayOfYear)

        // Convert longitude to hours and calculate solar noon
        val solarNoon = calculateSolarNoon(longitude, eqOfTime)

        // Calculate sunrise/sunset times
        val (sunrise, sunset) = calculateSunriseSunset(
            latitude,
            longitude,
            dayOfYear,
            eqOfTime,
            declination
        )

        // Calculate prayer times based on method
        val fajrAngle = getFajrAngle(calculationMethod)
        val ishaAngle = getIshaAngle(calculationMethod)
        val ishaInterval = getIshaInterval(calculationMethod)

        val fajrTime = calculateTimeFromAngle(longitude, -fajrAngle, eqOfTime, solarNoon)
        val dhuhrTime = adjustNoon(solarNoon)
        val asrTime = calculateAsrTime(solarNoon, latitude, declination, madhab)
        val maghribTime = adjustTwilight(sunset)
        val ishaTime = calculateIshaTime(
            sunset,
            latitude,
            declination,
            ishaInterval,
            ishaAngle,
            longitude,
            eqOfTime,
            solarNoon
        )

        return listOf(
            PrayerTime(PrayerName.FAJR, fajrTime),
            PrayerTime(PrayerName.DHUHR, dhuhrTime),
            PrayerTime(PrayerName.ASR, asrTime),
            PrayerTime(PrayerName.MAGHRIB, maghribTime),
            PrayerTime(PrayerName.ISHA, ishaTime)
        )
    }

    /**
     * Calculate the position of the sun (equation of time and declination)
     */
    private fun sunPosition(dayOfYear: Int): Pair<Double, Double> {
        val d = dayOfYear.toDouble()
        val gamma = 2 * PI / 365 * (d + 1)

        val declination = 23.45 * sin(gamma)
        val eqOfTime = -7.65 * sin(gamma) + 9.87 * sin(2 * gamma + 1.914)

        return eqOfTime to declination
    }

    /**
     * Calculate solar noon time in minutes from midnight
     */
    private fun calculateSolarNoon(longitude: Double, eqOfTime: Double): Double {
        return 720 - (longitude * 4) - eqOfTime
    }

    /**
     * Calculate sunrise and sunset times in minutes from midnight
     */
    private fun calculateSunriseSunset(
        latitude: Double,
        longitude: Double,
        dayOfYear: Int,
        eqOfTime: Double,
        declination: Double
    ): Pair<Double, Double> {
        val (hourAngle, _) = calculateHourAngle(latitude, declination)

        val sunrise = 720 - (hourAngle + longitude * 4) - eqOfTime
        val sunset = 720 + hourAngle - eqOfTime - (longitude * 4)

        return sunrise to sunset
    }

    /**
     * Calculate hour angle from latitude and declination
     */
    private fun calculateHourAngle(latitude: Double, declination: Double): Pair<Double, Double> {
        val latRad = Math.toRadians(latitude)
        val declRad = Math.toRadians(declination)

        val angle = -(sin(latRad) * sin(declRad) + 0.83) / (cos(latRad) * cos(declRad))
        val haAngleRaw = if (angle > 1) PI else if (angle < -1) PI else acos(angle)

        val haAngle = Math.toDegrees(haAngleRaw)
        return haAngle to haAngle / 15
    }

    /**
     * Calculate time based on solar angle
     */
    private fun calculateTimeFromAngle(
        longitude: Double,
        angle: Double,
        eqOfTime: Double,
        solarNoon: Double
    ): Date {
        val latRad = Math.toRadians(angle)
        val declRad = Math.toRadians(0.0) // Solar declination simplified

        val time = solarNoon - angle / 15
        return minutesToDate(time)
    }

    /**
     * Adjust Dhuhr time (solar noon + calculation method offset)
     */
    private fun adjustNoon(solarNoon: Double): Date {
        return minutesToDate(solarNoon + 1.0) // +1 minute after solar noon
    }

    /**
     * Calculate Asr time based on shadow length
     *
     * Hanafi: Shadow length = 2 * object length (recommended for Bangladesh)
     * Shafi: Shadow length = 1 * object length
     */
    private fun calculateAsrTime(
        solarNoon: Double,
        latitude: Double,
        declination: Double,
        madhab: Madhab
    ): Date {
        val latRad = Math.toRadians(latitude)
        val declRad = Math.toRadians(declination)
        val shafiRatio = 1.0
        val hanafiRatio = 2.0

        val ratio = if (madhab == Madhab.HANAFI) hanafiRatio else shafiRatio
        val sinLat = sin(latRad)
        val cosLat = cos(latRad)
        val sinDecl = sin(declRad)
        val cosDecl = cos(declRad)

        // More accurate Asr calculation using shadow length
        val tanLat = tan(latRad)
        val tanDecl = tan(declRad)
        val angle = atan(1 / (ratio + abs(tanLat - tanDecl)))
        val angleDeg = Math.toDegrees(angle)

        val asrTime = solarNoon + angleDeg / 15
        return minutesToDate(asrTime)
    }

    /**
     * Calculate Isha time
     * Karachi method uses angle-based calculation
     */
    private fun calculateIshaTime(
        sunset: Double,
        latitude: Double,
        declination: Double,
        interval: Double,
        angle: Double,
        longitude: Double,
        eqOfTime: Double,
        solarNoon: Double
    ): Date {
        val timeInMinutes = if (interval > 0) {
            sunset + interval
        } else {
            val latRad = Math.toRadians(latitude)
            val declRad = Math.toRadians(declination)
            val angleRad = Math.toRadians(angle)

            val cosValue = -sin(latRad) * sin(declRad) + cos(latRad) * cos(declRad) * cos(angleRad)
            val haAngleRaw = if (cosValue > 1) PI else if (cosValue < -1) PI else acos(cosValue)
            val haAngle = Math.toDegrees(haAngleRaw)

            solarNoon + haAngle / 15
        }
        return minutesToDate(timeInMinutes)
    }

    /**
     * Adjust Maghrib time (sunset + 1-2 minutes)
     */
    private fun adjustTwilight(sunset: Double): Date {
        return minutesToDate(sunset + 2.0) // +2 minutes after sunset
    }

    /**
     * Convert minutes from midnight to Date object
     */
    private fun minutesToDate(minutes: Double): Date {
        var hours = minutes / 60.0
        val hour = (hours % 24).toInt()
        val minute = ((hours - hour) * 60).toInt()

        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }.time
    }

    /**
     * Fajr angle for different calculation methods
     *
     * Karachi: 18° (最适合孟加拉国)
     * ISNA: 15° (North America)
     * Egypt: 19.5°
     */
    private fun getFajrAngle(method: CalculationMethod): Double {
        return when (method) {
            CalculationMethod.MWL -> 18.0
            CalculationMethod.ISNA -> 15.0
            CalculationMethod.EGYPT -> 19.5
            CalculationMethod.MAKKAH -> 18.5
            CalculationMethod.KARACHI -> 18.0
            CalculationMethod.TEHRAN -> 17.7
        }
    }

    /**
     * Isha angle for different calculation methods
     */
    private fun getIshaAngle(method: CalculationMethod): Double {
        return when (method) {
            CalculationMethod.MWL -> 17.0
            CalculationMethod.ISNA -> 15.0
            CalculationMethod.EGYPT -> 17.5
            CalculationMethod.MAKKAH -> -90.0 // Fixed time from sunset
            CalculationMethod.KARACHI -> 18.0
            CalculationMethod.TEHRAN -> 17.7
        }
    }

    /**
     * Isha interval (fixed minutes after sunset) if using fixed time
     */
    private fun getIshaInterval(method: CalculationMethod): Double {
        return when (method) {
            CalculationMethod.MAKKAH -> 90.0 // 90 minutes after sunset
            else -> 0.0
        }
    }

    companion object {
        const val SOLAR_NOON_OFFSET = 0.0
        const val MAGHRIB_OFFSET = 2.0
        const val FAJR_OFFSET = -1.0
    }
}