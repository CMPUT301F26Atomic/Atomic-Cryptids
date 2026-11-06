package com.example.myapplication

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LotteryValidatorTest {

    private val day = 86_400_000L

    @Test
    fun isValidWinnerCount_acceptsPositiveIntegers() {
        assertTrue(LotteryValidator.isValidWinnerCount("3"))
        assertFalse(LotteryValidator.isValidWinnerCount("0"))
        assertFalse(LotteryValidator.isValidWinnerCount("-1"))
        assertFalse(LotteryValidator.isValidWinnerCount("abc"))
        assertFalse(LotteryValidator.isValidWinnerCount(""))
    }

    @Test
    fun isValidPeriod_requiresBothDatesAndEndAfterStart() {
        assertTrue(LotteryValidator.isValidPeriod(10 * day, 12 * day))
        assertFalse(LotteryValidator.isValidPeriod(null, 12 * day))
        assertFalse(LotteryValidator.isValidPeriod(10 * day, null))
        assertFalse(LotteryValidator.isValidPeriod(12 * day, 12 * day))
        assertFalse(LotteryValidator.isValidPeriod(12 * day, 10 * day))
    }

    @Test
    fun isRegistrationOpen_insideWindowOnly() {
        val start = 10 * day
        val end = 12 * day
        assertTrue(LotteryValidator.isRegistrationOpen(11 * day, start, end))
        assertTrue(LotteryValidator.isRegistrationOpen(start, start, end))
        assertFalse(LotteryValidator.isRegistrationOpen(end, start, end))
        assertFalse(LotteryValidator.isRegistrationOpen(9 * day, start, end))
        assertFalse(LotteryValidator.isRegistrationOpen(13 * day, start, end))
    }

    @Test
    fun isRegistrationOpen_closedWhenWindowMissing() {
        assertFalse(LotteryValidator.isRegistrationOpen(11 * day, null, 12 * day))
        assertFalse(LotteryValidator.isRegistrationOpen(11 * day, 10 * day, null))
        assertFalse(LotteryValidator.isRegistrationOpen(11 * day, null, null))
    }
}
