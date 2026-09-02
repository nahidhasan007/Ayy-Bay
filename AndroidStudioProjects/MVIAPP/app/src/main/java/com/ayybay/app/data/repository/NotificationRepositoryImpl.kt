package com.ayybay.app.data.repository

import com.ayybay.app.data.local.AppNotificationDao
import com.ayybay.app.data.local.entity.AppNotificationEntity
import com.ayybay.app.domain.model.AppNotification
import com.ayybay.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val appNotificationDao: AppNotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<AppNotification>> =
        appNotificationDao.getAllNotifications().map { list -> list.map { it.toDomain() } }

    override fun getUnreadCount(): Flow<Int> = appNotificationDao.getUnreadCount()

    override suspend fun addNotification(notification: AppNotification): Long =
        appNotificationDao.insert(notification.toEntity())

    override suspend fun markRead(id: Long) = appNotificationDao.markRead(id)

    override suspend fun markAllRead() = appNotificationDao.markAllRead()

    private fun AppNotificationEntity.toDomain() = AppNotification(
        id = id,
        type = type,
        titleEn = titleEn,
        titleBn = titleBn,
        bodyEn = bodyEn,
        bodyBn = bodyBn,
        timestamp = timestamp,
        isRead = isRead,
        deepLinkRoute = deepLinkRoute
    )

    private fun AppNotification.toEntity() = AppNotificationEntity(
        id = id,
        type = type,
        titleEn = titleEn,
        titleBn = titleBn,
        bodyEn = bodyEn,
        bodyBn = bodyBn,
        timestamp = timestamp,
        isRead = isRead,
        deepLinkRoute = deepLinkRoute
    )
}
