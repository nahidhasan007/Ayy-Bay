package com.ayybay.app.presentation.screen

import androidx.compose.runtime.Composable
import com.ayybay.app.presentation.language.tr

@Composable
fun LoginScreen(
    isSigningIn: Boolean,
    error: String?,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onDismissError: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    AuthScaffold(
        title = tr("Welcome back", "আবার স্বাগতম"),
        subtitle = tr(
            "Sign in to sync your prayers, finances\nand saved links.",
            "নামাজ, হিসাব ও সংরক্ষিত লিংক সিঙ্ক করতে সাইন ইন করুন।"
        ),
        error = error,
        onDismissError = onDismissError,
        primaryAction = {
            GoogleSignInButton(
                text = tr("Continue with Google", "Google দিয়ে চালিয়ে যান"),
                isLoading = isSigningIn,
                onClick = onGoogleSignIn
            )
        },
        footer = {
            AuthFooterLink(
                text = tr("New to Jibon?", "জীবনে নতুন?"),
                actionText = tr("Sign up", "সাইন আপ করুন"),
                onClick = onNavigateToSignUp
            )
        },
        onContinueAsGuest = onContinueAsGuest
    )
}
