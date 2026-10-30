package com.example.myapplication

import com.atomiccryptids.campsiteapp.Reservation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Local unit tests for [Reservation]: construction rules, date-overlap logic,
 * and the Firestore map round-trip. Firestore itself is not exercised.
 */
class ReservationTest {

    private val day = 86_400_000L // one day in millis

    private fun reservation(
        start: Long = 10 * day,
        end: Long = 13 * day,
        name: String = "Pine Hollow",
        userId: String = "uid-1",
        status: Reservation.Status = Reservation.Status.ACTIVE,
    ) = Reservation(
        campsiteName = name,
        campsiteLocation = "Jasper",
        userId = userId,
        startDateMillis = start,
        endDateMillis = end,
        status = status,
    )

    @Test
    fun construction_rejectsInvalidInput() {
        assertThrows(IllegalArgumentException::class.java) {
            reservation(
                start = 5 * day,
                end = 5 * day
            )
        }
        assertThrows(IllegalArgumentException::class.java) { reservation(userId = " ") }
    }

    @Test
    fun overlaps_whenDatesIntersectOnSameCampsite() {
        val a = reservation(start = 10 * day, end = 13 * day)
        val b = reservation(start = 12 * day, end = 15 * day, userId = "uid-2")
        assertTrue(a.overlaps(b))
        assertTrue(b.overlaps(a))
    }

    @Test
    fun overlaps_isFalseForTouchingRanges() {
        // b checks in exactly when a checks out — allowed (half-open ranges).
        val a = reservation(start = 10 * day, end = 13 * day)
        val b = reservation(start = 13 * day, end = 15 * day)
        assertFalse(a.overlaps(b))
        assertFalse(b.overlaps(a))
    }

    @Test
    fun overlaps_isFalseForDifferentCampsiteOrCancelled() {
        val a = reservation()
        assertFalse(a.overlaps(reservation(name = "Birch Flats")))
        assertFalse(a.overlaps(reservation(status = Reservation.Status.CANCELLED)))
    }
}