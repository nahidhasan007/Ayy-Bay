package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.Transaction

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMonth: Int = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1,
    val selectedYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
)