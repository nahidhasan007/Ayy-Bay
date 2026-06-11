package com.ayybay.app.data

import com.ayybay.app.domain.model.*
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.util.Calendar
import java.util.Date
import com.batoulapps.adhan.CalculationMethod as AdhanMethod
import com.batoulapps.adhan.Madhab as AdhanMadhab

class PrayerTimeCalculator {

    fun calculatePrayerTimes(
        date: Date,
        latitude: Double,
        longitude: Double,
        calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
        madhab: Madhab = Madhab.HANAFI
    ): List<PrayerTime> {
        val coordinates = Coordinates(latitude, longitude)
        val cal = Calendar.getInstance().apply { time = date }
        val dateComponents = DateComponents(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        val params = toAdhanMethod(calculationMethod).parameters
        params.madhab = toAdhanMadhab(madhab)
        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        return listOf(
            PrayerTime(PrayerName.FAJR, prayerTimes.fajr),
            PrayerTime(PrayerName.DHUHR, prayerTimes.dhuhr),
            PrayerTime(PrayerName.ASR, prayerTimes.asr),
            PrayerTime(PrayerName.MAGHRIB, prayerTimes.maghrib),
            PrayerTime(PrayerName.ISHA, prayerTimes.isha)
        )
    }

    private fun toAdhanMethod(method: CalculationMethod): AdhanMethod = when (method) {
        CalculationMethod.KARACHI -> AdhanMethod.KARACHI
        CalculationMethod.ISNA -> AdhanMethod.NORTH_AMERICA
        CalculationMethod.EGYPT -> AdhanMethod.EGYPTIAN
        CalculationMethod.MAKKAH -> AdhanMethod.UMM_AL_QURA
        CalculationMethod.MWL -> AdhanMethod.MUSLIM_WORLD_LEAGUE
    }

    private fun toAdhanMadhab(madhab: Madhab): AdhanMadhab = when (madhab) {
        Madhab.HANAFI -> AdhanMadhab.HANAFI
        Madhab.SHAFI -> AdhanMadhab.SHAFI
    }
}