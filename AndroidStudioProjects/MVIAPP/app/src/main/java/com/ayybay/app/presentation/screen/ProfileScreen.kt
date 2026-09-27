package com.ayybay.app.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.BuildConfig
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.ui.theme.IslamicGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String?,
    isDeletingAccount: Boolean = false,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {}
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(tr("Delete account & data?", "অ্যাকাউন্ট ও ডেটা মুছবেন?")) },
            text = {
                Text(
                    tr(
                        "This permanently deletes everything stored on this device -- transactions, notes, prayer logs, Quran progress, bookmarks -- and signs you out. This can't be undone.",
                        "এতে এই ডিভাইসে সংরক্ষিত সবকিছু (লেনদেন, নোট, নামাজের হিসাব, কুরআন অগ্রগতি, বুকমার্ক) স্থায়ীভাবে মুছে যাবে এবং আপনি সাইন আউট হয়ে যাবেন। এটি ফিরিয়ে আনা যাবে না।"
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                    }
                ) {
                    Text(tr("Delete everything", "সব মুছে ফেলুন"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(tr("Cancel", "বাতিল করুন"))
                }
            }
        )
    }
    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = tr("Profile", "প্রোফাইল"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
                },
                actions = {
                    LanguageToggle(
                        modifier = Modifier.padding(end = 12.dp),
                        selectedColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        borderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                        dividerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(IslamicGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (userEmail != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column {
                Text(
                    text = tr("App Info", "অ্যাপ তথ্য"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column {
                        ProfileInfoRow(tr("Version", "ভার্সন"), BuildConfig.VERSION_NAME)
                        HorizontalDivider()
                        ProfileInfoRow(tr("Build number", "বিল্ড নম্বর"), BuildConfig.VERSION_CODE.toString())
                        HorizontalDivider()
                        ProfileInfoRow(
                            tr("Environment", "এনভায়রনমেন্ট"),
                            BuildConfig.ENVIRONMENT.replaceFirstChar { it.uppercase() }
                        )
                        HorizontalDivider()
                        ProfileInfoRow(tr("Package", "প্যাকেজ"), BuildConfig.APPLICATION_ID)
                    }
                }
            }

            TextButton(
                onClick = onOpenPrivacyPolicy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(tr("Privacy Policy", "প্রাইভেসি পলিসি"))
            }

            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(tr("Sign out", "সাইন আউট"))
            }

            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                enabled = !isDeletingAccount,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                if (isDeletingAccount) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.error)
                } else {
                    Icon(Icons.Filled.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(tr("Delete account & data", "অ্যাকাউন্ট ও ডেটা মুছুন"))
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
