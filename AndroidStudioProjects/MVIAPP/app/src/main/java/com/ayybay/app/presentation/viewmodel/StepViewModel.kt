package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.model.StepDay
import com.ayybay.app.domain.repository.StepRepository
import com.ayybay.app.presentation.mvi.StepUiState
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class StepViewModel(
    private val stepRepository: StepRepository
) : ViewModel() {

    private val todayKeyFlow = MutableStateFlow(Date().startOfDayMillis())
    private val monthStartFlow = MutableStateFlow(monthStartOf(todayKeyFlow.value))

    private val _uiState = MutableStateFlow(
        StepUiState(
            isSensorAvailable = stepRepository.isSensorAvailable,
            todayKey = todayKeyFlow.value,
            monthStartKey = monthStartFlow.value
        )
    )
    val uiState: StateFlow<StepUiState> = _uiState.asStateFlow()

    private var trackingJob: Job? = null

    init {
        observeGoal()
        observeWeek()
        observeMonth()
    }

    /** Call whenever the activity-recognition permission state is (re)checked. */
    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasPermission = granted) }
        if (granted) {
            // Credit steps walked since the last reading right away, before live updates start.
            viewModelScope.launch { runCatching { stepRepository.syncOnce() } }
        }
    }

    /** Streams live step updates while the tracker screen is visible. */
    fun startLiveTracking() {
        if (trackingJob?.isActive == true || !stepRepository.isSensorAvailable || !_uiState.value.hasPermission) return
        trackingJob = viewModelScope.launch { runCatching { stepRepository.trackLive() } }
    }

    fun stopLiveTracking() {
        trackingJob?.cancel()
        trackingJob = null
    }

    fun setDailyGoal(goal: Int) {
        if (goal <= 0) return
        viewModelScope.launch { stepRepository.setDailyGoal(goal) }
    }

    fun showPreviousMonth() = shiftMonth(-1)

    fun showNextMonth() {
        if (!_uiState.value.isCurrentMonth) shiftMonth(1)
    }

    /** Rolls "today" forward if midnight passed while the process stayed alive. */
    fun refreshDay() {
        val newKey = Date().startOfDayMillis()
        if (newKey == todayKeyFlow.value) return
        val followingCurrentMonth = _uiState.value.isCurrentMonth
        todayKeyFlow.value = newKey
        if (followingCurrentMonth) monthStartFlow.value = monthStartOf(newKey)
    }

    private fun shiftMonth(months: Int) {
        monthStartFlow.value = calendarAt(monthStartFlow.value).apply { add(Calendar.MONTH, months) }.timeInMillis
    }

    private fun observeGoal() {
        viewModelScope.launch {
            stepRepository.dailyGoal.catch { }.collect { goal ->
                _uiState.update { it.copy(dailyGoal = goal) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeWeek() {
        viewModelScope.launch {
            todayKeyFlow.flatMapLatest { todayKey ->
                stepRepository.observeRange(addDays(todayKey, -6), addDays(todayKey, 1))
                    .map { rows -> todayKey to rows }
            }.catch { }.collect { (todayKey, rows) ->
                val byKey = rows.associateBy { it.dateKey }
                val week = (-6..0).map { offset ->
                    val key = addDays(todayKey, offset)
                    byKey[key] ?: StepDay(key, 0)
                }
                _uiState.update {
                    it.copy(todayKey = todayKey, todaySteps = week.last().steps, week = week)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMonth() {
        viewModelScope.launch {
            monthStartFlow.flatMapLatest { monthStart ->
                stepRepository.observeRange(monthStart, nextMonthStart(monthStart))
                    .map { rows -> monthStart to rows }
            }.catch { }.collect { (monthStart, rows) ->
                _uiState.update {
                    it.copy(
                        monthStartKey = monthStart,
                        monthDays = rows,
                        isCurrentMonth = monthStart == monthStartOf(todayKeyFlow.value)
                    )
                }
            }
        }
    }

    private companion object {
        fun calendarAt(millis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = millis }

        fun addDays(key: Long, days: Int): Long = calendarAt(key).apply { add(Calendar.DAY_OF_YEAR, days) }.timeInMillis

        fun monthStartOf(key: Long): Long = calendarAt(key).apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis

        fun nextMonthStart(monthStart: Long): Long = calendarAt(monthStart).apply { add(Calendar.MONTH, 1) }.timeInMillis
    }
}
