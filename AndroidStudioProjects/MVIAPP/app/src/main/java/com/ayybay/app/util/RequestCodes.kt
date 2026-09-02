package com.ayybay.app.util

/**
 * Central registry of PendingIntent request codes.
 *
 * Every alarm/notification PendingIntent in the app must derive its request code from
 * here. Before this existed, prayer alarms used bare `PrayerName.ordinal` (0-4) and the
 * Adhan stop-button used the literal `1` -- both effectively unbounded from any other
 * subsystem, and one refactor away from colliding once more alarm types (Sehri, Iftar,
 * pre-adhan reminders, widget refresh) were added. Each subsystem below owns a disjoint
 * band so new alarm types can never collide with an existing one.
 */
object RequestCodes {
    private const val PRAYER_BASE = 1000          // 1000-1099: one per PrayerName ordinal (5 used)
    private const val PRAYER_REMINDER_BASE = 1100 // 1100-1199: pre-adhan reminders, one per PrayerName ordinal
    const val RAMADAN_SEHRI = 1200
    const val RAMADAN_IFTAR = 1201
    const val WIDGET_REFRESH = 1300
    const val ADHAN_STOP_ACTION = 1400
    private const val ALARM_BASE = 2000            // 2000+: one per user Alarm.id

    fun forPrayer(prayerOrdinal: Int): Int = PRAYER_BASE + prayerOrdinal

    fun forPrayerReminder(prayerOrdinal: Int): Int = PRAYER_REMINDER_BASE + prayerOrdinal

    fun forUserAlarm(alarmId: Long): Int = ALARM_BASE + alarmId.toInt()
}
