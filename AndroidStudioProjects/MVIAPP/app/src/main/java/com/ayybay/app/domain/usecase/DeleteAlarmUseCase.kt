package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.domain.repository.AlarmRepository

class DeleteAlarmUseCase(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm) = alarmRepository.deleteAlarm(alarm)
}
