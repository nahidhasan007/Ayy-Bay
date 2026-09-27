package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.AuthRepository

class DeleteAccountUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.deleteAccountAndData()
}
