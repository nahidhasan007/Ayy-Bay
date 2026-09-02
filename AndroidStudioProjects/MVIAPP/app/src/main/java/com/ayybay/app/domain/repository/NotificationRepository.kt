package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<AppNotification>>
    fun getUnreadCount(): Flow<Int>
    suspend fun addNotification(notification: AppNotification): Long
    suspend fun markRead(id: Long)
    suspend fun markAllRead()
}
