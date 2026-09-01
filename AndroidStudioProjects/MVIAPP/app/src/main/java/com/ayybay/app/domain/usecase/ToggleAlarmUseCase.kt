package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.repository.AlarmRepository

class ToggleAlarmUseCase(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke(id: Long, enabled: Boolean) = alarmRepository.setEnabled(id, enabled)
}
