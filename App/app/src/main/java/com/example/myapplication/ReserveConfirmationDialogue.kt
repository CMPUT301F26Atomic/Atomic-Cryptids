package com.example.myapplication

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.atomiccryptids.campsiteapp.Reservation

/**
 * Displays confirmation details after a successful
 * reservation or cancellation.
 *
 * @param reservation The reservation being confirmed.
 * @param isCancellation Whether this is a cancellation confirmation.
 * @param onDismiss Callback to close the dialog.
 * @param onDownloadCsv Callback to download the confirmation details.
 */
@Composable
fun ReservationConfirmationDialog(
    reservation: Reservation,
    isCancellation: Boolean,
    onDismiss: () -> Unit,
    onDownloadCsv: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isCancellation)
                    "Cancellation confirmed"
                else
                    "Reservation confirmed"
            )
        },
        text = {
            Text(
                "Campsite: ${reservation.campsiteName}\n" +
                        "Location: ${reservation.campsiteLocation}\n" +
                        "Check-in: ${formatUtcDate(reservation.startDateMillis)}\n" +
                        "Check-out: ${formatUtcDate(reservation.endDateMillis)}"
            )
        },
        confirmButton = {
            TextButton(onClick = onDownloadCsv) {
                Text("Download CSV")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}