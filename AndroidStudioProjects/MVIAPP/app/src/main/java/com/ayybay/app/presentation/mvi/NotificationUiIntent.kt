package com.ayybay.app.presentation.mvi

sealed class NotificationUiIntent {
    data class MarkRead(val id: Long) : NotificationUiIntent()
    object MarkAllRead : NotificationUiIntent()
}
