package com.ayybay.app.domain.model

data class AuthUser(
    val id: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)
