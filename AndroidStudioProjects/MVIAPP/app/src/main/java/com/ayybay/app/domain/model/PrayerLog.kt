package com.ayybay.app.domain.model

data class PrayerLog(
    val dateKey: Long,
    val prayerName: PrayerName,
    val isPrayed: Boolean
)

data class DayPrayerProgress(
    val dateKey: Long,
    val prayedCount: Int,
    val total: Int = PrayerName.entries.size
)
