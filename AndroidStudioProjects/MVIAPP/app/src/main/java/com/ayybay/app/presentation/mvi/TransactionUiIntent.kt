package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType

sealed class TransactionUiIntent {
    object LoadTransactions : TransactionUiIntent()
    object LoadIncomeTransactions : TransactionUiIntent()
    object LoadExpenseTransactions : TransactionUiIntent()

    data class AddTransaction(val transaction: Transaction) : TransactionUiIntent()
    data class UpdateTransaction(val transaction: Transaction) : TransactionUiIntent()
    data class DeleteTransaction(val transaction: Transaction) : TransactionUiIntent()
    data class FilterByMonth(val month: Int, val year: Int) : TransactionUiIntent()
    object ClearError : TransactionUiIntent()
}