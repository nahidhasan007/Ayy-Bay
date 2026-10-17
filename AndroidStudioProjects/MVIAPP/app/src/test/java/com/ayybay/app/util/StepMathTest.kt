package com.ayybay.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StepMathTest {

    @Test
    fun firstReadingHasNoBaselineSoAddsNothing() {
        assertEquals(0L, StepMath.delta(null, 5_000))
    }

    @Test
    fun laterReadingAddsTheDifference() {
        assertEquals(250L, StepMath.delta(1_000, 1_250))
        assertEquals(0L, StepMath.delta(1_000, 1_000))
    }

    @Test
    fun readingBelowBaselineMeansRebootAndCountsFromZero() {
        assertEquals(120L, StepMath.delta(48_000, 120))
    }

    @Test
    fun usesDefaultStrideWithoutHeight() {
        assertEquals(0.762, StepMath.strideMeters(null), 1e-9)
        assertEquals(0.762, StepMath.strideMeters(0.0), 1e-9)
        // 10,000 steps at 0.762 m = 7.62 km
        assertEquals(7.62, StepMath.kilometers(10_000, null), 1e-9)
    }

    @Test
    fun strideScalesWithHeight() {
        // 170 cm * 0.415 = 70.55 cm stride -> 10,000 steps = 7.055 km
        assertEquals(7.055, StepMath.kilometers(10_000, 170.0), 1e-9)
    }
}
