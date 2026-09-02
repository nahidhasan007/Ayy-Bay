package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.JobBookmarkRepository
import kotlinx.coroutines.flow.Flow

class GetBookmarkedJobIdsUseCase(
    private val jobBookmarkRepository: JobBookmarkRepository
) {
    operator fun invoke(): Flow<Set<Long>> = jobBookmarkRepository.getBookmarkedJobIds()
}
