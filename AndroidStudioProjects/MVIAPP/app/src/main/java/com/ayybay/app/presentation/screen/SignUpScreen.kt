package com.ayybay.app.presentation.screen

import androidx.compose.runtime.Composable

@Composable
fun SignUpScreen(
    isSigningIn: Boolean,
    error: String?,
    onGoogleSignUp: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onDismissError: () -> Unit
) {
    AuthScaffold(
        title = "Create your account",
        subtitle = "Join Jibon in seconds — no forms,\nno passwords to remember.",
        error = error,
        onDismissError = onDismissError,
        primaryAction = {
            GoogleSignInButton(
                text = "Sign up with Google",
                isLoading = isSigningIn,
                onClick = onGoogleSignUp
            )
        },
        footer = {
            AuthFooterLink(
                text = "Already have an account?",
                actionText = "Log in",
                onClick = onNavigateToLogin
            )
        }
    )
}
