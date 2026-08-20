package com.ayybay.app.presentation.language

enum class AppLanguage {
    EN, BN;

    companion object {
        fun fromCode(code: String?): AppLanguage = when (code) {
            "en" -> EN
            else -> BN
        }
    }
}

val AppLanguage.code: String
    get() = when (this) {
        AppLanguage.EN -> "en"
        AppLanguage.BN -> "bn"
    }
