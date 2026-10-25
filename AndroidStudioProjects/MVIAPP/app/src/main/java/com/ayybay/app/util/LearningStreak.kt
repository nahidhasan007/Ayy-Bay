package com.ayybay.app.util

object LearningStreak {

    /** Today as days since 1970-01-01 in the device's time zone; the day key for all learning progress. */
    fun today(): Long = java.time.LocalDate.now().toEpochDay()

    /**
     * Consecutive active days ending today. A streak isn't broken until a whole day passes with
     * no activity, so if today has none yet the count still runs through yesterday.
     */
    fun current(activeDays: Set<Long>, today: Long): Int {
        var day = when {
            today in activeDays -> today
            today - 1 in activeDays -> today - 1
            else -> return 0
        }
        var count = 0
        while (day in activeDays) {
            count++
            day--
        }
        return count
    }

    fun longest(activeDays: Set<Long>): Int {
        var best = 0
        var run = 0
        var previous: Long? = null
        for (day in activeDays.sorted()) {
            run = if (previous != null && day == previous + 1) run + 1 else 1
            if (run > best) best = run
            previous = day
        }
        return best
    }
}
