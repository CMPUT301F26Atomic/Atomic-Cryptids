package com.example.myapplication

import com.atomiccryptids.campsiteapp.Reservation
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ReservationValidatorTest {

    private val day = 86_400_000L
    private val availStart = 10 * day
    private val availEnd = 30 * day

    private fun existing(start: Long, end: Long, status: Reservation.Status = Reservation.Status.ACTIVE) =
        Reservation(
            campsiteId = "site-1",
            campsiteName = "Pine Hollow",
            campsiteLocation = "Jasper",
            userId = "other-uid",
            startDateMillis = start,
            endDateMillis = end,
            status = status,
        )

    private fun validate(
        start: Long?,
        end: Long?,
        existingReservations: List<Reservation> = emptyList(),
        windowStart: Long? = availStart,
        windowEnd: Long? = availEnd,
    ) = ReservationValidator.validateRange(start, end, windowStart, windowEnd, existingReservations)

    @Test
    fun validRange_insideWindow_noConflicts() {
        assertNull(validate(12 * day, 15 * day))
    }

    @Test
    fun missingDates_rejected() {
        assertNotNull(validate(null, 15 * day))
        assertNotNull(validate(12 * day, null))
    }

    @Test
    fun endNotAfterStart_rejected() {
        assertNotNull(validate(15 * day, 15 * day))
        assertNotNull(validate(15 * day, 12 * day))
    }

    @Test
    fun noAvailabilityWindow_rejected() {
        assertNotNull(validate(12 * day, 15 * day, windowStart = null, windowEnd = null))
    }

    @Test
    fun outsideAvailabilityWindow_rejected() {
        assertNotNull(validate(5 * day, 15 * day))
        assertNotNull(validate(25 * day, 35 * day))
    }

    @Test
    fun overlappingActiveReservation_rejected() {
        val booked = listOf(existing(14 * day, 18 * day))
        assertNotNull(validate(12 * day, 15 * day, booked))
    }

    @Test
    fun touchingBookedRange_allowed() {
        val booked = listOf(existing(14 * day, 18 * day))
        assertNull(validate(12 * day, 14 * day, booked))
        assertNull(validate(18 * day, 20 * day, booked))
    }

    @Test
    fun cancelledReservation_doesNotBlock() {
        val cancelled = listOf(existing(14 * day, 18 * day, Reservation.Status.CANCELLED))
        assertNull(validate(12 * day, 15 * day, cancelled))
    }
}
