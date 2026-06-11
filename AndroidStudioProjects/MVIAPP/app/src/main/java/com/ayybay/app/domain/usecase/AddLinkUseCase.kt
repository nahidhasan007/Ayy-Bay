package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.DailyLink
import com.ayybay.app.domain.repository.LinkRepository

class AddLinkUseCase(
    private val linkRepository: LinkRepository
) {
    suspend operator fun invoke(link: DailyLink): Long {
        return linkRepository.insertLink(link)
    }
}