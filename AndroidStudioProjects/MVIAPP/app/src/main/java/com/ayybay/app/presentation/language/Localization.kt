package com.ayybay.app.presentation.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalAppLanguage = compositionLocalOf { AppLanguage.BN }

/**
 * Returns [en] or [bn] depending on the app-wide language selection.
 * Use this at every user-facing text call site instead of hardcoding a single language.
 */
@Composable
fun tr(en: String, bn: String): String =
    if (LocalAppLanguage.current == AppLanguage.EN) en else bn
