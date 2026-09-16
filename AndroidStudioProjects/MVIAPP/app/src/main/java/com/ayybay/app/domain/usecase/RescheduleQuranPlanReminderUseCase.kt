package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.QuranReadingPlanRepository

/** Re-arms the reading-plan reminder alarm; used after device reboot clears all AlarmManager state. */
class RescheduleQuranPlanReminderUseCase(
    private val quranReadingPlanRepository: QuranReadingPlanRepository
) {
    suspend operator fun invoke() = quranReadingPlanRepository.rescheduleReminder()
}
