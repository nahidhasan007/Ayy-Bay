package com.ayybay.app.domain.model

enum class LearningCategory { ISLAMIC, VOCABULARY, GENERAL_KNOWLEDGE }

/**
 * One bite-sized daily lesson. The meaning of the fields depends on [category]:
 * - ISLAMIC: [arabic] is the text, [subline] its transliteration, [meaningEn]/[meaningBn] the translation.
 * - VOCABULARY: [titleEn] is the word, [subline] its pronunciation, [meaningEn]/[meaningBn] the meaning,
 *   [noteEn]/[noteBn] an example sentence.
 * - GENERAL_KNOWLEDGE: [titleEn]/[titleBn] is the question, [meaningEn]/[meaningBn] the answer,
 *   [noteEn]/[noteBn] an extra fact.
 */
data class LearningItem(
    val id: String,
    val category: LearningCategory,
    val tagEn: String,
    val tagBn: String,
    val titleEn: String,
    val titleBn: String,
    val meaningEn: String,
    val meaningBn: String,
    val arabic: String? = null,
    val subline: String? = null,
    val noteEn: String? = null,
    val noteBn: String? = null,
    val reference: String? = null
)

data class QuizQuestion(
    val id: String,
    val questionEn: String,
    val questionBn: String,
    val optionsEn: List<String>,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationBn: String
)

/** Outcome of a finished quiz for one day; only the best attempt of a day is kept. */
data class QuizResult(val score: Int, val total: Int)

data class LearningProgress(
    /** Lessons marked learned, as (epochDay, itemId) pairs. */
    val learned: Set<Pair<Long, String>> = emptySet(),
    val quizResults: Map<Long, QuizResult> = emptyMap(),
    val reminderEnabled: Boolean = false,
    /** Hour of day (0-23) for the reminder. */
    val reminderHour: Int = DEFAULT_REMINDER_HOUR
) {
    /** Every day with any learning activity: a lesson marked learned or a quiz finished. */
    val activeDays: Set<Long> get() = learned.mapTo(HashSet()) { it.first } + quizResults.keys

    companion object {
        const val DEFAULT_REMINDER_HOUR = 20
    }
}
