package com.ayybay.app.domain.model

/**
 * [repeatDays] uses java.util.Calendar day-of-week values (SUNDAY=1 .. SATURDAY=7).
 * An empty set means a one-time alarm that disables itself after it fires.
 */
data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatDays: Set<Int> = emptySet(),
    val vibrate: Boolean = true,
    val createdAt: Long = 0L
)
