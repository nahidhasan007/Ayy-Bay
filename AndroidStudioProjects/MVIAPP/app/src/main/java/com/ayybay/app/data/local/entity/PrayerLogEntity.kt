package com.ayybay.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "prayer_logs", primaryKeys = ["dateKey", "prayerName"])
data class PrayerLogEntity(
    val dateKey: Long,
    val prayerName: String,
    val isPrayed: Boolean
)
