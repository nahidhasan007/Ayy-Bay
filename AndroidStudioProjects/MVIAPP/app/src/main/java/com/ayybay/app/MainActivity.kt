package com.ayybay.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.screen.HomeScreen
import com.ayybay.app.presentation.viewmodel.TransactionViewModel
import com.ayybay.app.presentation.viewmodel.PrayerViewModel
import com.ayybay.app.ui.theme.MVIAPPTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val transactionViewModel: TransactionViewModel by viewModel()
    private val prayerViewModel: PrayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission
        requestNotificationPermission()

        setContent {
            val uiState by transactionViewModel.uiState.collectAsState()
            val prayerTimes by prayerViewModel.prayerTimes.collectAsState()

            MVIAPPTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HomeScreen(
                        uiState = uiState,
                        onAddTransaction = { transaction ->
                            transactionViewModel.handleIntent(
                                TransactionUiIntent.AddTransaction(
                                    transaction
                                )
                            )
                        },
                        onUpdateTransaction = { transaction ->
                            transactionViewModel.handleIntent(
                                TransactionUiIntent.UpdateTransaction(
                                    transaction
                                )
                            )
                        },
                        onDeleteTransaction = { transaction ->
                            transactionViewModel.handleIntent(
                                TransactionUiIntent.DeleteTransaction(
                                    transaction
                                )
                            )
                        },
                        prayerTimes = prayerTimes,
                        onTogglePrayerNotification = { prayerName, enabled ->
                            prayerViewModel.togglePrayerNotification(prayerName, enabled)
                        }
                    )
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Schedule notifications after permission is granted
            prayerViewModel.scheduleNotifications()
        }
    }

    private fun requestNotificationPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // Permission already granted, schedule notifications
                    prayerViewModel.scheduleNotifications()
                }
            }

            else -> {
                // Pre-Android 13, schedule notifications directly
                prayerViewModel.scheduleNotifications()
            }
        }
    }
}