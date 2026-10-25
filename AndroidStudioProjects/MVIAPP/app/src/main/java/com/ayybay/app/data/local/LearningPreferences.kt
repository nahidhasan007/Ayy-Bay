package com.ayybay.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ayybay.app.domain.model.LearningProgress
import com.ayybay.app.domain.model.QuizResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.learningDataStore by preferencesDataStore(name = "learning_prefs")

/**
 * Daily Learning progress. Entries are encoded as "epochDay|itemId" (lessons learned) and
 * "epochDay|score|total" (quiz results); a malformed entry is skipped rather than crashing.
 */
class LearningPreferences(private val context: Context) {

    private object Keys {
        val LEARNED = stringSetPreferencesKey("learned_entries")
        val QUIZ_RESULTS = stringSetPreferencesKey("quiz_results")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    }

    val progress: Flow<LearningProgress> = context.learningDataStore.data.map { prefs ->
        LearningProgress(
            learned = prefs[Keys.LEARNED].orEmpty().mapNotNullTo(HashSet()) { decodeLearned(it) },
            quizResults = decodeQuizResults(prefs),
            reminderEnabled = prefs[Keys.REMINDER_ENABLED] ?: false,
            reminderHour = prefs[Keys.REMINDER_HOUR] ?: LearningProgress.DEFAULT_REMINDER_HOUR
        )
    }

    suspend fun progressOnce(): LearningProgress = progress.first()

    suspend fun setLearned(epochDay: Long, itemId: String, learned: Boolean) {
        val entry = "$epochDay|$itemId"
        context.learningDataStore.edit { prefs ->
            val current = prefs[Keys.LEARNED].orEmpty()
            prefs[Keys.LEARNED] = if (learned) current + entry else current - entry
        }
    }

    /** Stores [result] for [epochDay], keeping the better of it and any earlier attempt that day. */
    suspend fun recordQuizResult(epochDay: Long, result: QuizResult) {
        context.learningDataStore.edit { prefs ->
            val existing = decodeQuizResults(prefs)
            val best = existing[epochDay]?.takeIf { it.score >= result.score } ?: result
            val updated = existing + (epochDay to best)
            prefs[Keys.QUIZ_RESULTS] = updated.mapTo(HashSet()) { (day, r) -> "$day|${r.score}|${r.total}" }
        }
    }

    suspend fun setReminder(enabled: Boolean, hour: Int) {
        context.learningDataStore.edit { prefs ->
            prefs[Keys.REMINDER_ENABLED] = enabled
            prefs[Keys.REMINDER_HOUR] = hour
        }
    }

    private fun decodeLearned(entry: String): Pair<Long, String>? {
        val parts = entry.split('|', limit = 2)
        val day = parts.getOrNull(0)?.toLongOrNull() ?: return null
        val id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() } ?: return null
        return day to id
    }

    private fun decodeQuizResults(prefs: Preferences): Map<Long, QuizResult> =
        prefs[Keys.QUIZ_RESULTS].orEmpty().mapNotNull { entry ->
            val parts = entry.split('|')
            val day = parts.getOrNull(0)?.toLongOrNull() ?: return@mapNotNull null
            val score = parts.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
            val total = parts.getOrNull(2)?.toIntOrNull() ?: return@mapNotNull null
            day to QuizResult(score, total)
        }.toMap()
}
