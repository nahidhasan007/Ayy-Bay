package com.ayybay.app.presentation.mvi

sealed class QuranPlanUiIntent {
    data class StartPlan(val durationDays: Int, val reminderHour: Int, val reminderMinute: Int) : QuranPlanUiIntent()
    data class UpdateReminderTime(val hour: Int, val minute: Int) : QuranPlanUiIntent()
    object CancelPlan : QuranPlanUiIntent()
}
