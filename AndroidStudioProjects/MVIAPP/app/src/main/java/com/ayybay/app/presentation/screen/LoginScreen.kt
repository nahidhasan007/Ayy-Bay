package com.ayybay.app.presentation.screen

import androidx.compose.runtime.Composable

@Composable
fun LoginScreen(
    isSigningIn: Boolean,
    error: String?,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onDismissError: () -> Unit
) {
    AuthScaffold(
        title = "Welcome back",
        subtitle = "Sign in to sync your prayers, finances\nand saved links.",
        error = error,
        onDismissError = onDismissError,
        primaryAction = {
            GoogleSignInButton(
                text = "Continue with Google",
                isLoading = isSigningIn,
                onClick = onGoogleSignIn
            )
        },
        footer = {
            AuthFooterLink(
                text = "New to Jibon?",
                actionText = "Sign up",
                onClick = onNavigateToSignUp
            )
        }
    )
}
