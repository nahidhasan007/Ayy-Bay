package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.DailyLink
import com.ayybay.app.domain.repository.LinkRepository
import kotlinx.coroutines.flow.Flow

class GetLinksByCategoryUseCase(
    private val linkRepository: LinkRepository
) {
    operator fun invoke(category: String): Flow<List<DailyLink>> {
        return linkRepository.getLinksByCategory(category)
    }
}