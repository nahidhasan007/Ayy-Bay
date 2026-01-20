package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetAllTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }
}