package com.ayybay.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ayybay.app.data.repository.AlarmRepositoryImpl.Companion.ACTION_ALARM_RING
import com.ayybay.app.data.repository.AlarmRepositoryImpl.Companion.EXTRA_ALARM_ID
import com.ayybay.app.receiver.AlarmReceiver
import com.ayybay.app.service.AlarmRingingService
import java.util.concurrent.TimeUnit

/** Shared Stop/Snooze behavior used by both the full-screen ring UI and the notification's action buttons. */
object AlarmRingActions {

    const val EXTRA_IS_SNOOZE = "is_snooze"
    private const val SNOOZE_MINUTES = 10
    private const val SNOOZE_REQUEST_CODE_OFFSET = 900_000

    fun stop(context: Context) {
        AlarmRingingService.stop(context)
    }

    fun snooze(context: Context, alarmId: Long) {
        AlarmRingingService.stop(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(SNOOZE_MINUTES.toLong())
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_RING
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_IS_SNOOZE, true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            SNOOZE_REQUEST_CODE_OFFSET + alarmId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }
}
