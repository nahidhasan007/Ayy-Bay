package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quran_read_days")
data class QuranReadDayEntity(
    @PrimaryKey val dateKey: Long,
    val surahsOpened: Int
)
