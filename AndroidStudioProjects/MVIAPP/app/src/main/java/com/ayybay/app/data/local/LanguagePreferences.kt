package com.ayybay.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.code
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.languageDataStore by preferencesDataStore(name = "language_prefs")

class LanguagePreferences(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
    }

    val language: Flow<AppLanguage> = context.languageDataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[Keys.LANGUAGE])
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.languageDataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language.code
        }
    }
}
