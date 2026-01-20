package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Long): Transaction?

    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun deleteTransactionById(id: Long)

    suspend fun getMonthlySummary(
        year: Int,
        month: Int
    ): com.ayybay.app.domain.model.MonthlySummary?
}