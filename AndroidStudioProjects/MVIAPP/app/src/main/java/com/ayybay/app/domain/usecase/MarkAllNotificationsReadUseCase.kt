package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.NotificationRepository

class MarkAllNotificationsReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke() = notificationRepository.markAllRead()
}
