package com.ayybay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_bookmarks")
data class JobBookmarkEntity(
    @PrimaryKey val jobId: Long,
    val bookmarkedAt: Long
)
