package com.ayybay.app.util

import com.ayybay.app.domain.model.HourlyForecast
import com.ayybay.app.domain.model.RainAlert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RainAlertLogicTest {

    private val hour = 3600L
    private val now = 1_700_000_000L - 1_700_000_000L % hour + 600 // 10 minutes into an hour

    private fun hourAt(offsetHours: Int, prob: Int, mm: Double = 1.0, code: Int = 61) =
        HourlyForecast(now - now % hour + offsetHours * hour, 28.0, prob, mm, code)

    @Test
    fun noAlertWhenProbabilityBelowThreshold() {
        val alert = RainAlertLogic.findRainAlert(listOf(hourAt(1, 40)), now, thresholdPercent = 60)
        assertNull(alert)
    }

    @Test
    fun alertsOnFirstHourMeetingThreshold() {
        val alert = RainAlertLogic.findRainAlert(
            listOf(hourAt(2, 90), hourAt(1, 70), hourAt(0, 10)), now, thresholdPercent = 60
        )
        assertNotNull(alert)
        assertEquals(now - now % hour + hour, alert!!.startSec)
        assertEquals(70, alert.probabilityPercent)
    }

    @Test
    fun currentHourCountsEvenThoughItStartedInThePast() {
        val alert = RainAlertLogic.findRainAlert(listOf(hourAt(0, 80)), now, thresholdPercent = 60)
        assertNotNull(alert)
    }

    @Test
    fun ignoresHoursBeyondLookaheadAndInThePast() {
        val hours = listOf(hourAt(-1, 95), hourAt(RainAlertLogic.LOOKAHEAD_HOURS, 95))
        assertNull(RainAlertLogic.findRainAlert(hours, now, thresholdPercent = 60))
    }

    @Test
    fun highProbabilityWithNoPrecipitationAndDryCodeIsIgnored() {
        val alert = RainAlertLogic.findRainAlert(listOf(hourAt(1, 80, mm = 0.0, code = 3)), now, thresholdPercent = 60)
        assertNull(alert)
    }

    @Test
    fun rainCodeAloneIsEnoughWhenAmountRoundsToZero() {
        val alert = RainAlertLogic.findRainAlert(listOf(hourAt(1, 80, mm = 0.0, code = 80)), now, thresholdPercent = 60)
        assertNotNull(alert)
    }

    @Test
    fun thunderstormIsFlaggedHeavy() {
        val alert = RainAlertLogic.findRainAlert(listOf(hourAt(1, 80, mm = 2.0, code = 95)), now, thresholdPercent = 60)!!
        assertTrue(alert.isThunderstorm)
        assertTrue(alert.isHeavy)
    }

    @Test
    fun heavyRainDependsOnHourlyAmount() {
        val light = RainAlertLogic.findRainAlert(listOf(hourAt(1, 80, mm = 2.0)), now, thresholdPercent = 60)!!
        val heavy = RainAlertLogic.findRainAlert(listOf(hourAt(1, 80, mm = 9.0)), now, thresholdPercent = 60)!!
        assertFalse(light.isHeavy)
        assertTrue(heavy.isHeavy)
    }

    @Test
    fun notifiesWhenNeverNotifiedBefore() {
        val alert = RainAlert(now, 80, 2.0, isThunderstorm = false, isHeavy = false)
        assertTrue(RainAlertLogic.shouldNotify(alert, null))
    }

    @Test
    fun staysQuietDuringCooldownForTheSameSpell() {
        val alert = RainAlert(now + hour, 80, 2.0, isThunderstorm = false, isHeavy = false)
        assertFalse(RainAlertLogic.shouldNotify(alert, lastNotifiedStartSec = now))
        assertFalse(RainAlertLogic.shouldNotify(alert, lastNotifiedStartSec = now + hour - RainAlertLogic.COOLDOWN_SEC + 1))
    }

    @Test
    fun notifiesAgainOnceCooldownHasPassed() {
        val alert = RainAlert(now + RainAlertLogic.COOLDOWN_SEC, 80, 2.0, isThunderstorm = false, isHeavy = false)
        assertTrue(RainAlertLogic.shouldNotify(alert, lastNotifiedStartSec = now))
    }
}
