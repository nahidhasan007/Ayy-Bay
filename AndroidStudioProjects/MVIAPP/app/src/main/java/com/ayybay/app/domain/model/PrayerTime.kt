package com.ayybay.app.domain.model

import java.util.Date

data class PrayerTime(
    val prayerName: PrayerName,
    val time: Date,
    val isEnabled: Boolean = true
)

enum class PrayerName(val displayName: String) {
    FAJR("Fajr"),
    DHUHR("Dhuhr"),
    ASR("Asr"),
    MAGHRIB("Maghrib"),
    ISHA("Isha")
}

data class PrayerSettings(
    val locationLatitude: Double? = 23.8103,
    val locationLongitude: Double? = 90.4125,
    val placeName: String? = null,
    val autoLocationEnabled: Boolean = false,
    val calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
    val madhab: Madhab = Madhab.HANAFI,
    val notificationsEnabled: Boolean = true,
    val fajrEnabled: Boolean = true,
    val dhuhrEnabled: Boolean = true,
    val asrEnabled: Boolean = true,
    val maghribEnabled: Boolean = true,
    val ishaEnabled: Boolean = true,
    val hijriOffset: Int = 0
) {
    fun isPrayerEnabled(prayerName: PrayerName): Boolean = when (prayerName) {
        PrayerName.FAJR -> fajrEnabled
        PrayerName.DHUHR -> dhuhrEnabled
        PrayerName.ASR -> asrEnabled
        PrayerName.MAGHRIB -> maghribEnabled
        PrayerName.ISHA -> ishaEnabled
    }

    fun withPrayerEnabled(prayerName: PrayerName, enabled: Boolean): PrayerSettings = when (prayerName) {
        PrayerName.FAJR -> copy(fajrEnabled = enabled)
        PrayerName.DHUHR -> copy(dhuhrEnabled = enabled)
        PrayerName.ASR -> copy(asrEnabled = enabled)
        PrayerName.MAGHRIB -> copy(maghribEnabled = enabled)
        PrayerName.ISHA -> copy(ishaEnabled = enabled)
    }
}

enum class CalculationMethod(val methodName: String) {
    MWL("Muslim World League"),
    ISNA("Islamic Society of North America"),
    EGYPT("Egyptian General Authority"),
    MAKKAH("Umm al-Qura University, Makkah"),
    KARACHI("University of Islamic Sciences, Karachi")
}

enum class Madhab {
    SHAFI,
    HANAFI
}