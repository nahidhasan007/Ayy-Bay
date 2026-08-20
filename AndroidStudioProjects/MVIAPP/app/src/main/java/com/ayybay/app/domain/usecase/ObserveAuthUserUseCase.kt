package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.AuthUser
import com.ayybay.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthUser?> = authRepository.currentUser
}
