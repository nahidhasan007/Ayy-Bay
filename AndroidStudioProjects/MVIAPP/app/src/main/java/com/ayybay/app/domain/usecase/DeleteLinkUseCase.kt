package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.DailyLink
import com.ayybay.app.domain.repository.LinkRepository

class DeleteLinkUseCase(
    private val linkRepository: LinkRepository
) {
    suspend operator fun invoke(link: DailyLink) {
        linkRepository.deleteLink(link)
    }
}