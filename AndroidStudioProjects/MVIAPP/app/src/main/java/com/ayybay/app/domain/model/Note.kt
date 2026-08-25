package com.ayybay.app.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val body: String,
    val isPinned: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
