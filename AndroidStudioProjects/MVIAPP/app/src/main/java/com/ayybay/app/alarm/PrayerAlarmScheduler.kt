package com.ayybay.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ayybay.app.data.PrayerTimeCalculator
import com.ayybay.app.domain.model.CalculationMethod
import com.ayybay.app.domain.model.Madhab
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.receiver.AzanNotificationReceiver
import java.util.Calendar
import java.util.Date

class PrayerAlarmScheduler(private val context: Context) {

    private val calculator = PrayerTimeCalculator()

    companion object {
        const val DEFAULT_LATITUDE = 23.8103
        const val DEFAULT_LONGITUDE = 90.4125
    }

    fun scheduleAllPrayers(
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
        madhab: Madhab = Madhab.HANAFI
    ) {
        val todayPrayers = calculator.calculatePrayerTimes(
            date = Date(),
            latitude = latitude,
            longitude = longitude,
            calculationMethod = calculationMethod,
            madhab = madhab
        )

        val tomorrowCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        val tomorrowPrayers = calculator.calculatePrayerTimes(
            date = tomorrowCalendar.time,
            latitude = latitude,
            longitude = longitude,
            calculationMethod = calculationMethod,
            madhab = madhab
        )

        val now = System.currentTimeMillis()
        todayPrayers.forEach { prayer ->
            val triggerTime = if (prayer.time.time > now) {
                prayer.time.time
            } else {
                tomorrowPrayers.find { it.prayerName == prayer.prayerName }?.time?.time ?: return@forEach
            }
            scheduleAlarm(prayer.prayerName, prayer.prayerName.displayName, triggerTime)
        }
    }

    fun scheduleNextOccurrence(
        prayerName: PrayerName,
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
        madhab: Madhab = Madhab.HANAFI
    ) {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        val prayers = calculator.calculatePrayerTimes(
            date = tomorrow.time,
            latitude = latitude,
            longitude = longitude,
            calculationMethod = calculationMethod,
            madhab = madhab
        )
        val prayer = prayers.find { it.prayerName == prayerName } ?: return
        scheduleAlarm(prayerName, prayerName.displayName, prayer.time.time)
    }

    private fun scheduleAlarm(prayerName: PrayerName, displayName: String, triggerTimeMs: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AzanNotificationReceiver::class.java).apply {
            putExtra("prayer_name", displayName)
            putExtra("prayer_time", triggerTimeMs)
            action = "com.ayybay.app.AZAN_NOTIFICATION"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.ordinal,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
        }
    }

    fun cancelAllPrayers() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        PrayerName.values().forEach { prayerName ->
            val intent = Intent(context, AzanNotificationReceiver::class.java).apply {
                action = "com.ayybay.app.AZAN_NOTIFICATION"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayerName.ordinal,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }
}