package com.ayybay.app.domain.repository

import android.content.Context
import com.ayybay.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    suspend fun signInWithGoogle(context: Context): Result<AuthUser>
    suspend fun signOut()
}
