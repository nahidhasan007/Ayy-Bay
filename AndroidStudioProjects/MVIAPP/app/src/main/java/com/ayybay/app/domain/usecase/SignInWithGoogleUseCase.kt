package com.ayybay.app.domain.usecase

import android.content.Context
import com.ayybay.app.domain.model.AuthUser
import com.ayybay.app.domain.repository.AuthRepository

class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(context: Context): Result<AuthUser> =
        authRepository.signInWithGoogle(context)
}
