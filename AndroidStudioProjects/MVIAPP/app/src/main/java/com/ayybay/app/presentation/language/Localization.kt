package com.ayybay.app.presentation.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalAppLanguage = compositionLocalOf { AppLanguage.BN }

/**
 * Non-composable counterpart to [tr], for call sites that can't read a CompositionLocal --
 * notifications, widgets, CSV/report text, anything posted from a BroadcastReceiver/Service/
 * WorkManager worker. Pass the language explicitly (read it from LanguagePreferences).
 */
fun trOf(language: AppLanguage, en: String, bn: String): String =
    if (language == AppLanguage.EN) en else bn

/**
 * Returns [en] or [bn] depending on the app-wide language selection.
 * Use this at every user-facing text call site instead of hardcoding a single language.
 */
@Composable
fun tr(en: String, bn: String): String = trOf(LocalAppLanguage.current, en, bn)
