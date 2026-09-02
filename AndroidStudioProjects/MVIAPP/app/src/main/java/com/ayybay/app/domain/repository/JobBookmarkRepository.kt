package com.ayybay.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface JobBookmarkRepository {
    fun getBookmarkedJobIds(): Flow<Set<Long>>
    suspend fun toggleBookmark(jobId: Long, bookmarked: Boolean)
}
