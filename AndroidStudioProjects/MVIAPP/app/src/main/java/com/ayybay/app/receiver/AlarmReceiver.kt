package com.ayybay.app.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ayybay.app.alarm.AlarmRingActions
import com.ayybay.app.alarm.AlarmRingActivity
import com.ayybay.app.data.repository.AlarmRepositoryImpl.Companion.EXTRA_ALARM_ID
import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.domain.repository.AlarmRepository
import com.ayybay.app.service.AlarmRingingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val alarmRepository: AlarmRepository by inject()

    companion object {
        const val NOTIFICATION_ID = 3001
        const val CHANNEL_ID = "alarm_ring_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return
        val isSnooze = intent.getBooleanExtra(AlarmRingActions.EXTRA_IS_SNOOZE, false)

        val pendingResult = goAsync()
        scope.launch {
            try {
                val alarm = alarmRepository.getAlarmById(alarmId)
                if (alarm != null) {
                    if (!isSnooze) {
                        if (alarm.repeatDays.isNotEmpty()) {
                            alarmRepository.scheduleAlarm(alarm)
                        } else {
                            alarmRepository.setEnabled(alarmId, false)
                        }
                    }
                    ring(context, alarm)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun ring(context: Context, alarm: Alarm) {
        AlarmRingingService.start(context, alarm)
        showFullScreenNotification(context, alarm)
    }

    private fun showFullScreenNotification(context: Context, alarm: Alarm) {
        createNotificationChannel(context)

        val fullScreenIntent = Intent(context, AlarmRingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarm.id.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val timeText = "%02d:%02d".format(alarm.hour, alarm.minute)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(alarm.label.ifBlank { "Alarm" })
            .setContentText(timeText)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm ring notifications"
                setSound(null, null) // AlarmRingingService plays the alarm sound itself
                enableVibration(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
