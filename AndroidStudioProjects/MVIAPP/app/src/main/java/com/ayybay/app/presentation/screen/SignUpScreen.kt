package com.ayybay.app.presentation.screen

import androidx.compose.runtime.Composable
import com.ayybay.app.presentation.language.tr

@Composable
fun SignUpScreen(
    isSigningIn: Boolean,
    error: String?,
    onGoogleSignUp: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onDismissError: () -> Unit
) {
    AuthScaffold(
        title = tr("Create your account", "আপনার অ্যাকাউন্ট তৈরি করুন"),
        subtitle = tr(
            "Join Jibon in seconds — no forms,\nno passwords to remember.",
            "কয়েক সেকেন্ডে জীবনে যুক্ত হোন — কোনো ফর্ম নেই,\nমনে রাখার মতো পাসওয়ার্ড নেই।"
        ),
        error = error,
        onDismissError = onDismissError,
        primaryAction = {
            GoogleSignInButton(
                text = tr("Sign up with Google", "Google দিয়ে সাইন আপ করুন"),
                isLoading = isSigningIn,
                onClick = onGoogleSignUp
            )
        },
        footer = {
            AuthFooterLink(
                text = tr("Already have an account?", "আগে থেকেই অ্যাকাউন্ট আছে?"),
                actionText = tr("Log in", "লগ ইন করুন"),
                onClick = onNavigateToLogin
            )
        }
    )
}
