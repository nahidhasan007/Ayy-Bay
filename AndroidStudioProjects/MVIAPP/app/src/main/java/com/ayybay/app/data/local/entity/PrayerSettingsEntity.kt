package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_settings")
data class PrayerSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val locationLatitude: Double?,
    val locationLongitude: Double?,
    val placeName: String? = null,
    val autoLocationEnabled: Boolean = false,
    val calculationMethod: String,
    val madhab: String,
    val notificationsEnabled: Boolean = true,
    val fajrEnabled: Boolean = true,
    val dhuhrEnabled: Boolean = true,
    val asrEnabled: Boolean = true,
    val maghribEnabled: Boolean = true,
    val ishaEnabled: Boolean = true,
    val hijriOffset: Int = 0
)