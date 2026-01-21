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
    val locationLatitude: Double? = null,
    val locationLongitude: Double? = null,
    val calculationMethod: CalculationMethod = CalculationMethod.MWL,
    val madhab: Madhab = Madhab.SHAFI,
    val notificationsEnabled: Boolean = true
)

enum class CalculationMethod(val methodName: String) {
    MWL("Muslim World League"),
    ISNA("Islamic Society of North America"),
    EGYPT("Egyptian General Authority"),
    MAKKAH("Umm al-Qura University, Makkah"),
    KARACHI("University of Islamic Sciences, Karachi"),
    TEHRAN("Institute of Geophysics, University of Tehran")
}

enum class Madhab {
    SHAFI,
    HANAFI
}