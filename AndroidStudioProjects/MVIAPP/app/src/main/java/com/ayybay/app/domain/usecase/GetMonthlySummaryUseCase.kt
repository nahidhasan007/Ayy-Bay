package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.TransactionRepository

class GetMonthlySummaryUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(year: Int, month: Int) = repository.getMonthlySummary(year, month)
}