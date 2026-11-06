package com.example.myapplication

import com.atomiccryptids.campsiteapp.Reservation

/**
 * Validates a requested reservation date range against the campsite's
 * availability window and the campsite's existing active reservations.
 * Dates are half-open: start inclusive, end exclusive.
 * Returns null when the range is valid, otherwise a user-facing error message.
 */
object ReservationValidator {

    fun validateRange(
        startDateMillis: Long?,
        endDateMillis: Long?,
        availableStartDate: Long?,
        availableEndDate: Long?,
        existingReservations: List<Reservation>,
    ): String? {
        if (startDateMillis == null || endDateMillis == null) {
            return "Please select both dates."
        }
        if (endDateMillis <= startDateMillis) {
            return "End date must be after start date."
        }
        if (availableStartDate == null || availableEndDate == null) {
            return "This campsite has no availability dates set."
        }
        if (startDateMillis < availableStartDate || endDateMillis > availableEndDate) {
            return "Selected dates are outside the campsite's availability."
        }
        val conflict = existingReservations.any {
            it.isActive() &&
                startDateMillis < it.endDateMillis &&
                it.startDateMillis < endDateMillis
        }
        if (conflict) {
            return "Those dates are already reserved."
        }
        return null
    }
}

fun formatUtcDate(millis: Long): String =
    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        .apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }
        .format(java.util.Date(millis))
