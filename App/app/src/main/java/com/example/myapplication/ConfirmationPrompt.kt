package com.example.myapplication

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * UI element which prompts the user to confirm or decline
 *
 * Resources:
 * https://dev.to/myougatheaxo/dialogs-in-jetpack-compose-alertdialog-bottomsheet-and-snackbar-3npo
 * https://stackoverflow.com/questions/77604456/creating-a-composable-confirm-dialog-box
 *
 * @param title title text of the prompt
 * @param message message text
 * @param onConfirm actions when confirmed
 * @param onDismiss actions when declined
 */
@Composable
fun ConfirmationPrompt(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecline,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDecline()
            }
            ) {
                Text("Cancel")
            }
        }
    )
}