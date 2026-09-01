package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.AlarmRepository

class RescheduleAllAlarmsUseCase(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke() = alarmRepository.rescheduleAllEnabledAlarms()
}
