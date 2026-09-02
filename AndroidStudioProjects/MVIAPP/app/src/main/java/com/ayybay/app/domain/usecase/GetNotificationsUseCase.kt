package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.AppNotification
import com.ayybay.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<AppNotification>> = notificationRepository.getAllNotifications()
}
