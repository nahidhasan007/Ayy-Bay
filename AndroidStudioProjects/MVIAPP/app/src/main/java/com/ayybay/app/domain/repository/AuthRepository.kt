package com.ayybay.app.domain.repository

import android.content.Context
import com.ayybay.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    suspend fun signInWithGoogle(context: Context): Result<AuthUser>
    suspend fun signOut()

    /** Wipes the signed-in session and every locally stored table (transactions, notes, prayer
     *  logs, Quran progress, job bookmarks, etc). There is no server-side account to delete --
     *  all of this app's user data lives only on-device -- so this is the full "delete my data"
     *  action required by Play Store account-deletion policy. */
    suspend fun deleteAccountAndData()
}
