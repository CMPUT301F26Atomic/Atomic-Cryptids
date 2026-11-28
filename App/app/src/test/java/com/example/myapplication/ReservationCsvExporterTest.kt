package com.example.myapplication

import com.atomiccryptids.campsiteapp.Reservation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservationCsvExporterTest {

    private fun createReservation(
        name: String = "Reid Lake",
        location: String = "YZF"
    ): Reservation {
        return Reservation(
            campsiteId = "campsite123",
            campsiteName = name,
            campsiteLocation = location,
            userId = "user123",
            startDateMillis = 1797465600000L,
            endDateMillis = 1798070400000L
        )
    }

    @Test
    fun reservationConfirmationContainsCorrectDetails() {
        val reservation = createReservation()

        val csv = ReservationCsvExporter.generate(
            reservation,
            isCancellation = false
        )

        val expectedRow = "\"Reid Lake\",\"YZF\"," +
                "\"${formatUtcDate(reservation.startDateMillis)}\"," +
                "\"${formatUtcDate(reservation.endDateMillis)}\"," +
                "\"Confirmed\""

        assertEquals(expectedRow, csv.lines()[1])
    }

    @Test
    fun cancellationConfirmationContainsCorrectStatus() {
        val reservation = createReservation()

        val csv = ReservationCsvExporter.generate(
            reservation,
            isCancellation = true
        )

        val expectedRow = "\"Reid Lake\",\"YZF\"," +
                "\"${formatUtcDate(reservation.startDateMillis)}\"," +
                "\"${formatUtcDate(reservation.endDateMillis)}\"," +
                "\"Cancelled\""

        assertEquals(expectedRow, csv.lines()[1])
    }

    @Test
    fun campsiteNameContainingCommaIsEscaped() {
        val reservation = createReservation(
            name = "Reid Lake, North"
        )

        val csv = ReservationCsvExporter.generate(
            reservation,
            isCancellation = false
        )

        assertTrue(
            csv.lines()[1].startsWith("\"Reid Lake, North\",")
        )
    }

    @Test
    fun locationContainingQuotationMarksIsEscaped() {
        val reservation = createReservation(
            location = "Yellowknife \"North\""
        )

        val csv = ReservationCsvExporter.generate(
            reservation,
            isCancellation = false
        )

        assertTrue(
            csv.contains("\"Yellowknife \"\"North\"\"\"")
        )
    }

    @Test
    fun csvContainsCorrectHeaders() {
        val reservation = createReservation()

        val csv = ReservationCsvExporter.generate(
            reservation,
            isCancellation = false
        )

        assertEquals(
            "Campsite,Location,Check-in,Check-out,Status",
            csv.lineSequence().first()
        )
    }
}