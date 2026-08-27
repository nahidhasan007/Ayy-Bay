package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_times",
    indices = [Index(value = ["date", "prayerName"], unique = true)]
)
data class PrayerTimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prayerName: String,
    val time: Long,
    val date: Long,
    val isEnabled: Boolean = true
)

