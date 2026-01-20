package com.ayybay.app.data.mapper

import com.ayybay.app.data.local.TransactionEntity
import com.ayybay.app.domain.model.Transaction

object TransactionMapper {

    fun toDomain(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id,
            type = entity.type,
            amount = entity.amount,
            category = entity.category,
            description = entity.description,
            date = entity.date
        )
    }

    fun toEntity(domain: Transaction): TransactionEntity {
        return TransactionEntity(
            id = domain.id,
            type = domain.type,
            amount = domain.amount,
            category = domain.category,
            description = domain.description,
            date = domain.date
        )
    }

    fun toDomainList(entities: List<TransactionEntity>): List<Transaction> {
        return entities.map { toDomain(it) }
    }
}