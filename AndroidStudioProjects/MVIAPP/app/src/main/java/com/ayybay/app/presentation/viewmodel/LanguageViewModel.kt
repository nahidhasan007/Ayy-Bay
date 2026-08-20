package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.data.local.LanguagePreferences
import com.ayybay.app.presentation.language.AppLanguage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val languagePreferences: LanguagePreferences
) : ViewModel() {

    val language: StateFlow<AppLanguage> = languagePreferences.language.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppLanguage.BN
    )

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            languagePreferences.setLanguage(language)
        }
    }
}
