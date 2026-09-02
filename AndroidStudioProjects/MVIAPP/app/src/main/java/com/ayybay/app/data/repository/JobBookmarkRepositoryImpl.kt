package com.ayybay.app.data.repository

import com.ayybay.app.data.local.JobBookmarkDao
import com.ayybay.app.data.local.entity.JobBookmarkEntity
import com.ayybay.app.domain.repository.JobBookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JobBookmarkRepositoryImpl(
    private val jobBookmarkDao: JobBookmarkDao
) : JobBookmarkRepository {

    override fun getBookmarkedJobIds(): Flow<Set<Long>> =
        jobBookmarkDao.getBookmarkedJobIds().map { it.toSet() }

    override suspend fun toggleBookmark(jobId: Long, bookmarked: Boolean) {
        if (bookmarked) {
            jobBookmarkDao.bookmark(JobBookmarkEntity(jobId = jobId, bookmarkedAt = System.currentTimeMillis()))
        } else {
            jobBookmarkDao.unbookmark(jobId)
        }
    }
}
