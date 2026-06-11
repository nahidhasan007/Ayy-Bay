package com.ayybay.app.domain.model

data class DailyLink(
    val id: Long = 0,
    val title: String,
    val description: String,
    val url: String,
    val category: LinkCategory,
    val addedDate: Long = 0L
)