package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_days")
data class StepDayEntity(
    @PrimaryKey val dateKey: Long,
    val steps: Int
)
