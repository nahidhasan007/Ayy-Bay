package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.LearningItem
import com.ayybay.app.domain.model.LearningProgress
import com.ayybay.app.domain.model.QuizQuestion
import com.ayybay.app.domain.model.QuizResult

data class LearningUiState(
    val items: List<LearningItem> = emptyList(),
    val learnedTodayIds: Set<String> = emptySet(),
    val streak: Int = 0,
    val longestStreak: Int = 0,
    /** Distinct lessons ever marked learned. */
    val totalLearned: Int = 0,
    /** Today's best finished quiz, or null if not taken yet. */
    val quizResultToday: QuizResult? = null,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = LearningProgress.DEFAULT_REMINDER_HOUR,
    /** The quiz currently being played; null when none is in progress. */
    val quiz: QuizSession? = null
) {
    val learnedTodayCount: Int get() = items.count { it.id in learnedTodayIds }
}

data class QuizSession(
    val questions: List<QuizQuestion>,
    val currentIndex: Int = 0,
    /** Chosen option per question; null until answered. */
    val answers: List<Int?>,
    val isFinished: Boolean = false
) {
    val currentQuestion: QuizQuestion get() = questions[currentIndex]
    val isCurrentAnswered: Boolean get() = answers[currentIndex] != null
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    val score: Int get() = questions.indices.count { answers[it] == questions[it].correctIndex }
}
