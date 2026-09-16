package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Singleton row (id always 1) -- only one reading plan can be active at a time. */
@Entity(tableName = "quran_reading_plan")
data class QuranReadingPlanEntity(
    @PrimaryKey val id: Int = 1,
    val durationDays: Int,
    val startDateKey: Long,
    val reminderHour: Int,
    val reminderMinute: Int,
    val lastMissedNotifiedDateKey: Long = 0L
)
