package com.ayybay.app.domain.model

data class SurahProgress(
    val surahNumber: Int,
    val isCompleted: Boolean,
    val completedAt: Long = 0L
)

data class QuranReadDay(
    val dateKey: Long,
    val surahsOpened: Int
)
