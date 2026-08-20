package com.ayybay.app.data.repository

import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.ayybay.app.data.local.AuthPreferences
import com.ayybay.app.domain.model.AuthUser
import com.ayybay.app.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class AuthRepositoryImpl(
    private val authPreferences: AuthPreferences,
    private val webClientId: String
) : AuthRepository {

    override val currentUser: Flow<AuthUser?> = authPreferences.currentUser

    override suspend fun signInWithGoogle(context: Context): Result<AuthUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = CredentialManager.create(context).getCredential(context, request)
            val credential = response.credential

            check(
                credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) { "Unexpected credential type: ${credential.type}" }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val claims = decodeIdTokenPayload(googleIdTokenCredential.idToken)

            val user = AuthUser(
                id = claims.getString("sub"),
                displayName = claims.optString("name").ifBlank { null } ?: googleIdTokenCredential.displayName,
                email = claims.optString("email").ifBlank { null },
                photoUrl = claims.optString("picture").ifBlank { null }
                    ?: googleIdTokenCredential.profilePictureUri?.toString()
            )
            authPreferences.saveUser(user)
            Result.success(user)
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        authPreferences.clearUser()
    }

    private fun decodeIdTokenPayload(idToken: String): JSONObject {
        val payload = idToken.split(".")[1]
        val decoded = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        return JSONObject(String(decoded, Charsets.UTF_8))
    }
}
