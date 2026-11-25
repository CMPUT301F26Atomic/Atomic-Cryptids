package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class LotteryDrawTest {

    private val entrants = listOf("a", "b", "c", "d", "e")

    @Test
    fun pickWinners_allWinWhenFewerEntrantsThanSpots() {
        assertEquals(entrants, LotteryDraw.pickWinners(entrants, 5))
        assertEquals(entrants, LotteryDraw.pickWinners(entrants, 10))
    }

    @Test
    fun pickWinners_selectsExactCountFromEntrants() {
        val winners = LotteryDraw.pickWinners(entrants, 2, Random(42))
        assertEquals(2, winners.size)
        assertEquals(2, winners.distinct().size)
        assertTrue(entrants.containsAll(winners))
    }

    @Test
    fun pickWinners_emptyOrZero() {
        assertEquals(emptyList<String>(), LotteryDraw.pickWinners(emptyList(), 3))
        assertEquals(emptyList<String>(), LotteryDraw.pickWinners(entrants, 0))
    }

    @Test
    fun isDrawDue_onlyAfterRegistrationEnds() {
        val end = 1000L
        assertTrue(LotteryDraw.isDrawDue(1000L, end, false))
        assertTrue(LotteryDraw.isDrawDue(2000L, end, false))
        assertFalse(LotteryDraw.isDrawDue(999L, end, false))
        assertFalse(LotteryDraw.isDrawDue(2000L, end, true))
        assertFalse(LotteryDraw.isDrawDue(2000L, null, false))
    }
}
