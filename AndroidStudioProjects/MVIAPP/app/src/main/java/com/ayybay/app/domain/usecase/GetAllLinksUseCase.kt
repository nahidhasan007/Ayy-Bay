package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.DailyLink
import com.ayybay.app.domain.repository.LinkRepository
import kotlinx.coroutines.flow.Flow

class GetAllLinksUseCase(
    private val linkRepository: LinkRepository
) {
    operator fun invoke(): Flow<List<DailyLink>> {
        return linkRepository.getAllLinks()
    }
}