package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlarmsUseCase(
    private val alarmRepository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> = alarmRepository.getAllAlarms()
}
