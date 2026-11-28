package com.example.myapplication

import com.atomiccryptids.campsiteapp.Reservation

/**
 * Generates CSV confirmation documents for reservations
 * and cancellations.
 *
 *  The following code was written with assistance from OpenAI, ChatGPT when prompted with
 *  "How can I allow a user to save and export a csv of their reservation information"
 */
object ReservationCsvExporter {

    /**
     * Converts reservation details into CSV format.
     */
    fun generate(
        reservation: Reservation,
        isCancellation: Boolean
    ): String {
        val status = if (isCancellation) "Cancelled" else "Confirmed"

        val headers = listOf(
            "Campsite",
            "Location",
            "Check-in",
            "Check-out",
            "Status"
        )

        val values = listOf(
            reservation.campsiteName,
            reservation.campsiteLocation,
            formatUtcDate(reservation.startDateMillis),
            formatUtcDate(reservation.endDateMillis),
            status
        )

        return headers.joinToString(",") + "\n" +
                values.joinToString(",") { escapeCsv(it) } + "\n"
    }

    /**
     * Escapes special characters within CSV fields.
     */
    private fun escapeCsv(value: String): String {
        return "\"" + value.replace("\"", "\"\"") + "\""
    }
}