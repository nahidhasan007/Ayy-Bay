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
    val calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
    val madhab: Madhab = Madhab.HANAFI,
    val notificationsEnabled: Boolean = true
)

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