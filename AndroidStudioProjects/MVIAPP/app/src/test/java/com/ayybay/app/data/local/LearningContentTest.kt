package com.ayybay.app.data.local

import com.ayybay.app.domain.model.LearningCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningContentTest {

    @Test
    fun everyItemIdIsUnique() {
        val ids = LearningCategory.entries.flatMap { LearningContent.itemsFor(it) }.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun everyCategoryHasContentAndIslamicItemsCarryArabic() {
        LearningCategory.entries.forEach { assertTrue(LearningContent.itemsFor(it).isNotEmpty()) }
        assertTrue(LearningContent.itemsFor(LearningCategory.ISLAMIC).all { !it.arabic.isNullOrBlank() })
    }

    @Test
    fun todaysItemsHaveOnePerCategory() {
        val items = LearningContent.todaysItems(20_000)
        assertEquals(LearningCategory.entries, items.map { it.category })
    }

    @Test
    fun lessonRotatesDailyAndWrapsAround() {
        val size = LearningContent.itemsFor(LearningCategory.VOCABULARY).size
        val today = LearningContent.itemFor(LearningCategory.VOCABULARY, 20_000)
        assertNotEquals(today, LearningContent.itemFor(LearningCategory.VOCABULARY, 20_001))
        assertEquals(today, LearningContent.itemFor(LearningCategory.VOCABULARY, 20_000L + size))
    }

    @Test
    fun rotationHandlesDaysBeforeTheEpoch() {
        val item = LearningContent.itemFor(LearningCategory.ISLAMIC, -1)
        assertTrue(item in LearningContent.itemsFor(LearningCategory.ISLAMIC))
    }

    @Test
    fun quizHasFiveDistinctQuestionsWithValidAnswers() {
        val questions = LearningContent.quizFor(20_000)
        assertEquals(LearningContent.QUIZ_QUESTIONS_PER_DAY, questions.size)
        assertEquals(questions.size, questions.map { it.id }.toSet().size)
        questions.forEach {
            assertEquals(it.optionsEn.size, it.optionsBn.size)
            assertTrue(it.correctIndex in it.optionsEn.indices)
        }
    }

    @Test
    fun consecutiveDaysDoNotRepeatQuizQuestions() {
        val day1 = LearningContent.quizFor(20_000).map { it.id }.toSet()
        val day2 = LearningContent.quizFor(20_001).map { it.id }.toSet()
        assertTrue(day1.intersect(day2).isEmpty())
    }
}
