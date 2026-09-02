package com.ayybay.app.domain.model

data class AppNotification(
    val id: Long = 0,
    val type: String,
    val titleEn: String,
    val titleBn: String,
    val bodyEn: String,
    val bodyBn: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val deepLinkRoute: String? = null
)
