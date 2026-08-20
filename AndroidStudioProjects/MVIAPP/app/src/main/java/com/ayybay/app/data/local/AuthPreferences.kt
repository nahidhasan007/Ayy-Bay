package com.ayybay.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ayybay.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val EMAIL = stringPreferencesKey("email")
        val PHOTO_URL = stringPreferencesKey("photo_url")
    }

    val currentUser: Flow<AuthUser?> = context.authDataStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID] ?: return@map null
        AuthUser(
            id = id,
            displayName = prefs[Keys.DISPLAY_NAME],
            email = prefs[Keys.EMAIL],
            photoUrl = prefs[Keys.PHOTO_URL]
        )
    }

    suspend fun saveUser(user: AuthUser) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.USER_ID] = user.id
            user.displayName?.let { prefs[Keys.DISPLAY_NAME] = it } ?: prefs.remove(Keys.DISPLAY_NAME)
            user.email?.let { prefs[Keys.EMAIL] = it } ?: prefs.remove(Keys.EMAIL)
            user.photoUrl?.let { prefs[Keys.PHOTO_URL] = it } ?: prefs.remove(Keys.PHOTO_URL)
        }
    }

    suspend fun clearUser() {
        context.authDataStore.edit { it.clear() }
    }
}
