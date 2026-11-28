package com.example.myapplication

import android.content.Context
import android.net.Uri
import java.io.IOException

/**
 * Writes reservation confirmation CSV data to a file
 * selected by the user through the Android file picker.
 *
 * The following code was written with assistance from OpenAI, ChatGPT when prompted with
 * "How can I allow a user to save and export a csv of their reservation information"
 */
object ReservationCsvDownload {

    /**
     * Saves CSV content to the selected file URI.
     */
    fun save(
        context: Context,
        uri: Uri,
        csvContent: String,
        onResult: (String) -> Unit
    ) {
        try {
            val outputStream = context.contentResolver
                .openOutputStream(uri)
                ?: throw IOException("Unable to open file")

            outputStream.use {
                it.write(csvContent.toByteArray(Charsets.UTF_8))
            }

            onResult("CSV downloaded successfully")
        } catch (e: Exception) {
            onResult("Error saving CSV: ${e.message}")
        }
    }
}