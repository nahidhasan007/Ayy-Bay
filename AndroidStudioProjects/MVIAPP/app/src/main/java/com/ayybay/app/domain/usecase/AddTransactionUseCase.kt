package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.repository.TransactionRepository

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        return repository.insertTransaction(transaction)
    }
}