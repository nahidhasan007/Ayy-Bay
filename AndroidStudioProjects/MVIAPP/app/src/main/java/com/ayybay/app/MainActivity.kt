package com.ayybay.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.screen.HomeScreen
import com.ayybay.app.presentation.viewmodel.TransactionViewModel
import com.ayybay.app.presentation.viewmodel.PrayerViewModel
import com.ayybay.app.service.AdhanForegroundService
import com.ayybay.app.ui.theme.MVIAPPTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ayybay.app.alarm.PrayerAlarmScheduler

class MainActivity : ComponentActivity() {

    private val transactionViewModel: TransactionViewModel by viewModel()
    private val prayerViewModel: PrayerViewModel by viewModel()
    private lateinit var prayerAlarmScheduler: PrayerAlarmScheduler

    private var isFirstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission
        requestNotificationPermission()

        // Request exact alarm permission (Android 12+)
        requestExactAlarmPermission()

        // Initialize prayer alarm scheduler
        prayerAlarmScheduler = PrayerAlarmScheduler(this)

        setContent {
            val uiState by transactionViewModel.uiState.collectAsState()
            val prayerTimes by prayerViewModel.prayerTimes.collectAsState()

            MVIAPPTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column {
                        TestAdhanButton()
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

        // Setup prayer alarms on first launch & play welcome Adhan
        if (isFirstLaunch) {
            setupPrayerAlarms()
            isFirstLaunch = false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Schedule notifications after permission is granted
            checkAndSchedulePrayers()
        }
    }

    private val exactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // User returned from exact alarm settings, schedule notifications
        checkAndSchedulePrayers()
    }

    private fun requestNotificationPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // Permission already granted, schedule notifications
                    checkAndSchedulePrayers()
                }
            }

            else -> {
                // Pre-Android 13, schedule notifications directly
                checkAndSchedulePrayers()
            }
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request user to grant exact alarm permission
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                exactAlarmPermissionLauncher.launch(intent)
            }
        }
    }

    private fun checkAndSchedulePrayers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                prayerViewModel.scheduleNotifications()
            }
        } else {
            // Pre-Android 12, schedule notifications directly
            prayerViewModel.scheduleNotifications()
        }
    }

    private fun setupPrayerAlarms() {
        // Schedule all 5 daily prayers
        prayerAlarmScheduler.scheduleAllPrayers()

        // Play welcome Adhan immediately (plays for 15 seconds)
        AdhanForegroundService.startAdhan(this, "Welcome to Ayy Bay App", 15)
    }

    @Composable
    private fun WelcomeMessage() {
        Text(
            text = "🕌 Welcome to Adhan App\n\nPrayer Times:\n• Fajr: 5:00 AM\n• Johr: 1:00 PM\n• Asar: 5:00 PM\n• Magrib: 6:10 PM\n• Esha: 8:00 PM",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFE8F5E9))
                .padding(16.dp)
        )
    }

    @Composable
    fun TestAdhanButton() {
        Button(
            onClick = {
                // Manually trigger Adhan playback for testing
                AdhanForegroundService.startAdhan(this@MainActivity, "Test Adhan", 15)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(
                    color = Color(0xFF047857),
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF047857), // Emerald-700
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                "🕌 Play Adhan",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}