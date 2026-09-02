package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.JobBookmarkRepository

class ToggleJobBookmarkUseCase(
    private val jobBookmarkRepository: JobBookmarkRepository
) {
    suspend operator fun invoke(jobId: Long, bookmarked: Boolean) =
        jobBookmarkRepository.toggleBookmark(jobId, bookmarked)
}
