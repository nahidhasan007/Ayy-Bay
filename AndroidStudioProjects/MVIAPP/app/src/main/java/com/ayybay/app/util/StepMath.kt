package com.ayybay.app.util

object StepMath {

    /** Average adult stride, used until the user has entered a height. */
    const val DEFAULT_STRIDE_METERS = 0.762

    /** Common walking-stride estimate: ~41.5% of body height. */
    private const val STRIDE_PER_HEIGHT = 0.415

    /**
     * Steps to add for a new TYPE_STEP_COUNTER reading. The sensor reports steps since the
     * last reboot, so a smaller reading than before means the device restarted and the counter
     * began again from zero. The very first reading has no baseline, so it contributes nothing.
     */
    fun delta(last: Long?, current: Long): Long = when {
        last == null -> 0L
        current >= last -> current - last
        else -> current
    }

    fun strideMeters(heightCm: Double?): Double =
        if (heightCm != null && heightCm > 0) heightCm * STRIDE_PER_HEIGHT / 100.0 else DEFAULT_STRIDE_METERS

    fun kilometers(steps: Long, heightCm: Double?): Double = steps * strideMeters(heightCm) / 1000.0
}
