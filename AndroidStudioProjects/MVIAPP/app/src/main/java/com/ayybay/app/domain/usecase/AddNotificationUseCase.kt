package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.AppNotification
import com.ayybay.app.domain.repository.NotificationRepository

class AddNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notification: AppNotification): Long =
        notificationRepository.addNotification(notification)
}
