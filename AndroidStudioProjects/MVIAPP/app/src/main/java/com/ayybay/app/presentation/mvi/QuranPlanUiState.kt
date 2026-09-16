package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.QuranReadingPlanStatus

data class QuranPlanUiState(
    val status: QuranReadingPlanStatus? = null,
    val isLoading: Boolean = true
)
