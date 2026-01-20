package com.ayybay.app.domain.model

data class MonthlySummary(
    val month: String,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)