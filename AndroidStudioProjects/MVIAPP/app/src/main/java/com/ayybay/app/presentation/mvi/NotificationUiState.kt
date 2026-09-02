package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.AppNotification

data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0
)
