package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah_progress")
data class SurahProgressEntity(
    @PrimaryKey val surahNumber: Int,
    val isCompleted: Boolean,
    val completedAt: Long
)
