package com.ayybay.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LearningStreakTest {

    @Test
    fun noActivityMeansNoStreak() {
        assertEquals(0, LearningStreak.current(emptySet(), today = 100))
    }

    @Test
    fun countsConsecutiveDaysEndingToday() {
        assertEquals(3, LearningStreak.current(setOf(98, 99, 100), today = 100))
    }

    @Test
    fun streakSurvivesUntilTodayEndsWithoutActivity() {
        assertEquals(2, LearningStreak.current(setOf(98, 99), today = 100))
    }

    @Test
    fun missingAWholeDayBreaksTheStreak() {
        assertEquals(0, LearningStreak.current(setOf(97, 98), today = 100))
    }

    @Test
    fun gapsSplitTheStreak() {
        assertEquals(2, LearningStreak.current(setOf(90, 91, 92, 99, 100), today = 100))
    }

    @Test
    fun longestFindsTheBestRun() {
        assertEquals(3, LearningStreak.longest(setOf(1, 2, 3, 10, 11, 20)))
        assertEquals(0, LearningStreak.longest(emptySet()))
        assertEquals(1, LearningStreak.longest(setOf(5)))
    }
}
