package com.ayybay.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ayybay.app.domain.usecase.SchedulePrayerNotificationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AzanBootReceiver : BroadcastReceiver(), KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val schedulePrayerNotificationsUseCase: SchedulePrayerNotificationsUseCase by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        val action = intent?.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.QUICKBOOT_POWERON") {
            scope.launch {
                try {
                    schedulePrayerNotificationsUseCase()
                } catch (e: Exception) {
                    // Log error if needed
                }
            }
        }
    }
}