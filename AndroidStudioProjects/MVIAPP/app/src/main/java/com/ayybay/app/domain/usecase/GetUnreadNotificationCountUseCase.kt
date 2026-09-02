package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetUnreadNotificationCountUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<Int> = notificationRepository.getUnreadCount()
}
