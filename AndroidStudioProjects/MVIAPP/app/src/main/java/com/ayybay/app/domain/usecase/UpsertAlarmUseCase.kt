package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.domain.repository.AlarmRepository

class UpsertAlarmUseCase(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm): Long = alarmRepository.upsertAlarm(alarm)
}
