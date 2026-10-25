package com.ayybay.app.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayybay.app.data.local.LearningContent
import com.ayybay.app.data.local.LearningPreferences
import com.ayybay.app.domain.model.QuizResult
import com.ayybay.app.presentation.mvi.LearningUiState
import com.ayybay.app.presentation.mvi.QuizSession
import com.ayybay.app.util.LearningStreak
import com.ayybay.app.worker.LearningReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LearningViewModel(
    private val learningPreferences: LearningPreferences,
    private val appContext: Context
) : ViewModel() {

    private val today = MutableStateFlow(LearningStreak.today())
    private val quiz = MutableStateFlow<QuizSession?>(null)

    val uiState: StateFlow<LearningUiState> = combine(
        learningPreferences.progress, today, quiz
    ) { progress, day, session ->
        val items = LearningContent.todaysItems(day)
        LearningUiState(
            items = items,
            learnedTodayIds = progress.learned.filter { it.first == day }.mapTo(HashSet()) { it.second },
            streak = LearningStreak.current(progress.activeDays, day),
            longestStreak = LearningStreak.longest(progress.activeDays),
            totalLearned = progress.learned.mapTo(HashSet()) { it.second }.size,
            quizResultToday = progress.quizResults[day],
            reminderEnabled = progress.reminderEnabled,
            reminderHour = progress.reminderHour,
            quiz = session
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LearningUiState())

    /** Rolls "today" forward if midnight passed while the process stayed alive. */
    fun refreshDay() {
        val newDay = LearningStreak.today()
        if (newDay != today.value) {
            today.value = newDay
            quiz.value = null
        }
    }

    fun setLearned(itemId: String, learned: Boolean) {
        viewModelScope.launch { learningPreferences.setLearned(today.value, itemId, learned) }
    }

    fun startQuiz() {
        val questions = LearningContent.quizFor(today.value)
        quiz.value = QuizSession(questions = questions, answers = List(questions.size) { null })
    }

    fun selectAnswer(optionIndex: Int) {
        quiz.update { session ->
            if (session == null || session.isFinished || session.isCurrentAnswered) session
            else session.copy(
                answers = session.answers.toMutableList().also { it[session.currentIndex] = optionIndex }
            )
        }
    }

    /** Moves to the next question, or finishes the quiz and records its score after the last one. */
    fun nextQuestion() {
        val session = quiz.value ?: return
        if (!session.isCurrentAnswered || session.isFinished) return
        if (session.isLastQuestion) {
            quiz.value = session.copy(isFinished = true)
            viewModelScope.launch {
                learningPreferences.recordQuizResult(today.value, QuizResult(session.score, session.questions.size))
            }
        } else {
            quiz.value = session.copy(currentIndex = session.currentIndex + 1)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val hour = uiState.value.reminderHour
            learningPreferences.setReminder(enabled, hour)
            if (enabled) LearningReminderWorker.schedule(appContext, hour) else LearningReminderWorker.cancel(appContext)
        }
    }

    fun setReminderHour(hour: Int) {
        viewModelScope.launch {
            val enabled = uiState.value.reminderEnabled
            learningPreferences.setReminder(enabled, hour)
            if (enabled) LearningReminderWorker.schedule(appContext, hour)
        }
    }
}
