package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_settings")
data class PrayerSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val locationLatitude: Double?,
    val locationLongitude: Double?,
    val calculationMethod: String,
    val madhab: String,
    val notificationsEnabled: Boolean = true
)