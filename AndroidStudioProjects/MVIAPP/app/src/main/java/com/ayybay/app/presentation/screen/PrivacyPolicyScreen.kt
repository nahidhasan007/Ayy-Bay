package com.ayybay.app.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.language.tr

/**
 * Bundled in-app copy of the same policy hosted at store-assets/privacy-policy.html --
 * keep the two in sync when either changes. The hosted copy is what goes in the Play
 * Console "Privacy policy" URL field; this screen is so users don't need network access
 * to read it, and so review can inspect it without leaving the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = tr("Privacy Policy", "প্রাইভেসি পলিসি"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(PRIVACY_POLICY_LAST_UPDATED, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            PolicySection(
                title = "Overview",
                body = "Jibon (\"the app\") is developed for the Bangladeshi Muslim community and covers prayer " +
                    "times, personal finance tracking, government job listings, Islamic websites, religious books, " +
                    "and Quran reading tracking. This policy explains what data the app touches and how it is used."
            )

            PolicySection(
                title = "Data stored only on your device",
                body = "All of your content -- transactions and finance records, notes, prayer/Salah logs, Quran " +
                    "reading progress and streaks, saved job bookmarks, alarms, and app notifications -- is stored " +
                    "locally on your device only, using a local database. None of it is uploaded to, or accessible " +
                    "from, any server operated by us. Uninstalling the app permanently deletes it, and you can " +
                    "delete it at any time from Profile > Delete account & data."
            )

            PolicySection(
                title = "Google Sign-In",
                body = "If you choose to sign in, the app uses Google Sign-In (Android Credential Manager) to " +
                    "read your Google account's name, email address, and profile photo, so the app can greet you " +
                    "and show you're signed in. This exchange happens directly between your device and Google; we " +
                    "never see or store your Google password. Your name/email/photo are cached locally on-device " +
                    "so you stay signed in between app launches. You can use the app without signing in at all via " +
                    "\"Explore the app as Guest\"."
            )

            PolicySection(
                title = "Location",
                body = "With your permission, the app reads your device's approximate or precise location to " +
                    "calculate accurate local prayer times and Qibla direction, and may resolve that location to a " +
                    "city name for display. Location is used on-device for this calculation only, is not stored " +
                    "beyond your current settings, and is not transmitted to us. If permission is not granted, the " +
                    "app falls back to a default location (Dhaka, Bangladesh)."
            )

            PolicySection(
                title = "Contacts",
                body = "The optional Phone Book feature reads your device's contacts so you can quickly find and " +
                    "call a saved number from within the app. Contacts are read on-demand for display only, are " +
                    "never copied into the app's own storage, and are never transmitted anywhere."
            )

            PolicySection(
                title = "Notifications and alarms",
                body = "The app schedules local notifications and alarms (e.g. for prayer/Adhan reminders and " +
                    "Quran reading reminders) using your device's operating system. These are generated and " +
                    "handled entirely on-device."
            )

            PolicySection(
                title = "What we don't do",
                body = "We do not run ads, do not use any analytics or crash-reporting SDK, and do not sell or " +
                    "share your data with third parties. There is no backend server for this app -- everything " +
                    "described above stays on your device or goes directly through Google's own sign-in service."
            )

            PolicySection(
                title = "Data deletion",
                body = "You can permanently delete all locally stored data and sign out at any time from Profile " +
                    "> Delete account & data inside the app, or simply uninstall the app."
            )

            PolicySection(
                title = "Children's privacy",
                body = "The app is not directed at children and does not knowingly collect data from children."
            )

            PolicySection(
                title = "Changes to this policy",
                body = "If this policy changes, the \"last updated\" date above will change and the new version " +
                    "will be available both in the app and at the hosted URL listed in the Play Store listing."
            )

            PolicySection(
                title = "Contact",
                body = "Questions about this policy or your data can be sent to: $PRIVACY_POLICY_CONTACT_EMAIL"
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}

private const val PRIVACY_POLICY_LAST_UPDATED = "Last updated: September 27, 2026"

private const val PRIVACY_POLICY_CONTACT_EMAIL = "nahidhasan99679@gmail.com"
