package com.ayybay.app.domain.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Date
)

enum class TransactionType {
    INCOME,
    EXPENSE
}