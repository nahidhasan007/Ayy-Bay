package com.ayybay.app

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.navigation.AppNavigation
import com.ayybay.app.presentation.viewmodel.AlarmViewModel
import com.ayybay.app.presentation.viewmodel.AuthViewModel
import com.ayybay.app.presentation.viewmodel.HealthViewModel
import com.ayybay.app.presentation.viewmodel.JobsViewModel
import com.ayybay.app.presentation.viewmodel.LanguageViewModel
import com.ayybay.app.presentation.viewmodel.LinkViewModel
import com.ayybay.app.presentation.viewmodel.NotificationViewModel
import com.ayybay.app.presentation.viewmodel.NoteViewModel
import com.ayybay.app.presentation.viewmodel.PhoneBookViewModel
import com.ayybay.app.presentation.viewmodel.PrayerViewModel
import com.ayybay.app.presentation.viewmodel.TrackerViewModel
import com.ayybay.app.presentation.viewmodel.TransactionViewModel
import com.ayybay.app.ui.theme.MVIAPPTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModel()
    private val transactionViewModel: TransactionViewModel by viewModel()
    private val prayerViewModel: PrayerViewModel by viewModel()
    private val linkViewModel: LinkViewModel by viewModel()
    private val languageViewModel: LanguageViewModel by viewModel()
    private val noteViewModel: NoteViewModel by viewModel()
    private val trackerViewModel: TrackerViewModel by viewModel()
    private val healthViewModel: HealthViewModel by viewModel()
    private val alarmViewModel: AlarmViewModel by viewModel()
    private val phoneBookViewModel: PhoneBookViewModel by viewModel()
    private val jobsViewModel: JobsViewModel by viewModel()
    private val notificationViewModel: NotificationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission()
        requestExactAlarmPermission()

        setContent {
            val language by languageViewModel.language.collectAsState()
            CompositionLocalProvider(LocalAppLanguage provides language) {
                MVIAPPTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        AppNavigation(
                            authViewModel = authViewModel,
                            transactionViewModel = transactionViewModel,
                            prayerViewModel = prayerViewModel,
                            linkViewModel = linkViewModel,
                            noteViewModel = noteViewModel,
                            trackerViewModel = trackerViewModel,
                            healthViewModel = healthViewModel,
                            alarmViewModel = alarmViewModel,
                            phoneBookViewModel = phoneBookViewModel,
                            jobsViewModel = jobsViewModel,
                            notificationViewModel = notificationViewModel
                        )
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) checkAndSchedulePrayers()
    }

    private val exactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        checkAndSchedulePrayers()
    }

    private fun requestNotificationPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    checkAndSchedulePrayers()
                }
            }
            else -> checkAndSchedulePrayers()
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
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
            prayerViewModel.scheduleNotifications()
        }
    }
}