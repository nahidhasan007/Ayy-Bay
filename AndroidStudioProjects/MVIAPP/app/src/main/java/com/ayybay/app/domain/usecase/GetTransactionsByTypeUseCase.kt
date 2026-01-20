package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType
import com.ayybay.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsByTypeUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(type: TransactionType): Flow<List<Transaction>> {
        return repository.getTransactionsByType(type)
    }
}