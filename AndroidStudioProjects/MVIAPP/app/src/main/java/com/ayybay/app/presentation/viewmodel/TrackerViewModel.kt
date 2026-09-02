package com.ayybay.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.domain.model.DayPrayerProgress
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.QuranReadDay
import com.ayybay.app.domain.usecase.GetQuranProgressUseCase
import com.ayybay.app.domain.usecase.GetQuranStreakUseCase
import com.ayybay.app.domain.usecase.GetQuranWeeklyReadingUseCase
import com.ayybay.app.domain.usecase.GetTodayPrayerLogUseCase
import com.ayybay.app.domain.usecase.GetWeeklyPrayerProgressUseCase
import com.ayybay.app.domain.usecase.MarkSurahReadUseCase
import com.ayybay.app.domain.usecase.ToggleSurahCompleteUseCase
import com.ayybay.app.domain.usecase.TogglePrayerLogUseCase
import com.ayybay.app.presentation.mvi.TrackerUiIntent
import com.ayybay.app.presentation.mvi.TrackerUiState
import com.ayybay.app.util.startOfDayMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

class TrackerViewModel(
    private val getTodayPrayerLogUseCase: GetTodayPrayerLogUseCase,
    private val togglePrayerLogUseCase: TogglePrayerLogUseCase,
    private val getWeeklyPrayerProgressUseCase: GetWeeklyPrayerProgressUseCase,
    private val getQuranProgressUseCase: GetQuranProgressUseCase,
    private val toggleSurahCompleteUseCase: ToggleSurahCompleteUseCase,
    private val markSurahReadUseCase: MarkSurahReadUseCase,
    private val getQuranWeeklyReadingUseCase: GetQuranWeeklyReadingUseCase,
    private val getQuranStreakUseCase: GetQuranStreakUseCase
) : ViewModel() {

    private var todayKey = Date().startOfDayMillis()
    private var weekStartKey = todayKey - 6 * DAY_MILLIS

    private val _uiState = MutableStateFlow(TrackerUiState(todayDateKey = todayKey))
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private var todayLogsJob: Job? = null
    private var weeklyProgressJob: Job? = null
    private var quranWeeklyJob: Job? = null

    init {
        observeTodayPrayerLogs()
        observeWeeklyPrayerProgress()
        observeQuranProgress()
        observeQuranWeeklyReading()
        refreshStreak()
    }

    fun handleIntent(intent: TrackerUiIntent) {
        when (intent) {
            is TrackerUiIntent.TogglePrayer -> togglePrayer(intent.prayerName, intent.isPrayed)
            is TrackerUiIntent.ToggleSurahComplete -> toggleSurah(intent.surahNumber, intent.completed)
            is TrackerUiIntent.MarkSurahOpened -> markSurahOpened(intent.surahNumber)
        }
    }

    /**
     * Re-derives today/this-week's keys if the calendar day has rolled over since the last
     * load (previously `todayKey`/`weekStartKey` were computed once at construction and never
     * revisited, so a long-lived process kept showing yesterday's data past midnight). Safe to
     * call often -- it's a no-op until the day changes.
     */
    fun refreshDay() {
        val newKey = Date().startOfDayMillis()
        if (newKey != todayKey) {
            todayKey = newKey
            weekStartKey = todayKey - 6 * DAY_MILLIS
            _uiState.value = _uiState.value.copy(todayDateKey = todayKey)
            observeTodayPrayerLogs()
            observeWeeklyPrayerProgress()
            observeQuranWeeklyReading()
            refreshStreak()
        }
    }

    private fun observeTodayPrayerLogs() {
        todayLogsJob?.cancel()
        todayLogsJob = viewModelScope.launch {
            getTodayPrayerLogUseCase(todayKey).catch { }.collect { logs ->
                val map = PrayerName.entries.associateWith { name ->
                    logs.find { it.prayerName == name }?.isPrayed ?: false
                }
                _uiState.value = _uiState.value.copy(todayPrayerLogs = map)
            }
        }
    }

    private fun observeWeeklyPrayerProgress() {
        weeklyProgressJob?.cancel()
        weeklyProgressJob = viewModelScope.launch {
            getWeeklyPrayerProgressUseCase(weekStartKey).catch { }.collect { progress ->
                _uiState.value = _uiState.value.copy(weeklyPrayerProgress = normalizePrayerWeek(progress))
            }
        }
    }

    private fun observeQuranProgress() {
        viewModelScope.launch {
            getQuranProgressUseCase().catch { }.collect { progress ->
                _uiState.value = _uiState.value.copy(quranProgress = progress)
            }
        }
    }

    private fun observeQuranWeeklyReading() {
        quranWeeklyJob?.cancel()
        quranWeeklyJob = viewModelScope.launch {
            getQuranWeeklyReadingUseCase(weekStartKey).catch { }.collect { days ->
                _uiState.value = _uiState.value.copy(quranWeeklyReading = normalizeReadWeek(days))
            }
        }
    }

    private fun togglePrayer(prayerName: PrayerName, isPrayed: Boolean) {
        viewModelScope.launch {
            togglePrayerLogUseCase(todayKey, prayerName, isPrayed)
        }
    }

    private fun toggleSurah(surahNumber: Int, completed: Boolean) {
        viewModelScope.launch {
            toggleSurahCompleteUseCase(surahNumber, completed)
        }
    }

    private fun markSurahOpened(surahNumber: Int) {
        viewModelScope.launch {
            markSurahReadUseCase(surahNumber, todayKey)
            refreshStreak()
        }
    }

    private fun refreshStreak() {
        viewModelScope.launch {
            val streak = getQuranStreakUseCase(todayKey, DAY_MILLIS)
            _uiState.value = _uiState.value.copy(quranStreak = streak)
        }
    }

    private fun normalizePrayerWeek(progress: List<DayPrayerProgress>): List<DayPrayerProgress> {
        val byKey = progress.associateBy { it.dateKey }
        return (0..6).map { offset ->
            val key = todayKey - (6 - offset) * DAY_MILLIS
            byKey[key] ?: DayPrayerProgress(dateKey = key, prayedCount = 0)
        }
    }

    private fun normalizeReadWeek(days: List<QuranReadDay>): List<QuranReadDay> {
        val byKey = days.associateBy { it.dateKey }
        return (0..6).map { offset ->
            val key = todayKey - (6 - offset) * DAY_MILLIS
            byKey[key] ?: QuranReadDay(dateKey = key, surahsOpened = 0)
        }
    }
}
