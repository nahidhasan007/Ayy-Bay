package com.ayybay.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ayybay.app.domain.model.TransactionType
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Date
)