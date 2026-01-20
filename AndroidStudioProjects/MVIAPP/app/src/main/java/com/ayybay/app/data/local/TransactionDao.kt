package com.ayybay.app.data.local

import androidx.room.*
import com.ayybay.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query(
        """
        SELECT type, SUM(amount) as total 
        FROM transactions 
        WHERE strftime('%Y', date/1000, 'unixepoch', 'localtime') = :year 
        AND strftime('%m', date/1000, 'unixepoch', 'localtime') = :month 
        GROUP BY type
    """
    )
    suspend fun getMonthlyTotals(year: String, month: String): List<MonthlyTotal>

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}

data class MonthlyTotal(
    val type: TransactionType,
    val total: Double
)