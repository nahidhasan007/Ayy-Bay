package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.NotificationRepository

class MarkNotificationReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(id: Long) = notificationRepository.markRead(id)
}
