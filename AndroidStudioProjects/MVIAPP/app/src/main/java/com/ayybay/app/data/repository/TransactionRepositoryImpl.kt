package com.ayybay.app.data.repository

import com.ayybay.app.data.local.TransactionDao
import com.ayybay.app.data.local.TransactionEntity
import com.ayybay.app.data.mapper.TransactionMapper
import com.ayybay.app.domain.model.MonthlySummary
import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType
import com.ayybay.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { TransactionMapper.toDomain(it) }
        }
    }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type).map { entities ->
            entities.map { TransactionMapper.toDomain(it) }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        val entity = transactionDao.getTransactionById(id)
        return if (entity != null) TransactionMapper.toDomain(entity) else null
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(
            TransactionMapper.toEntity(transaction)
        )
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(
            TransactionMapper.toEntity(transaction)
        )
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(
            TransactionMapper.toEntity(transaction)
        )
    }

    override suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    override suspend fun getMonthlySummary(year: Int, month: Int): MonthlySummary? {
        val monthStr = month.toString().padStart(2, '0')
        val totals = transactionDao.getMonthlyTotals(year.toString(), monthStr)

        val totalIncome = totals.find { it.type == TransactionType.INCOME }?.total ?: 0.0
        val totalExpense = totals.find { it.type == TransactionType.EXPENSE }?.total ?: 0.0

        val monthName = SimpleDateFormat("MMMM", Locale.getDefault())
            .format(Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1)
            }.time)

        return MonthlySummary(
            month = monthName,
            year = year,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = totalIncome - totalExpense
        )
    }
}