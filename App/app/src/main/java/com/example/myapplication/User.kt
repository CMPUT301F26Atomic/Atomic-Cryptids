package com.atomiccryptids.campsiteapp
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Retrieves all campsites from Firestore.
 *
 * Converts each campsite document into a Campsite object,
 * including its location, capacity, and availability dates.
 * Campsites are returned without filtering.
 *
 * @param onResult Callback that receives the list of retrieved campsites.
 * @param onError Callback that receives an error message if retrieval fails.
 */

interface User {
    val uid: String
    val username: String
    val role: String

    fun searchCampsites(
        onResult: (List<Campsite>) -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("campsites")
            .get()
            .addOnSuccessListener { result ->
                val campsites = result.documents.map { doc ->
                    Campsite(
                        id = doc.id,
                        capacity = (doc.getLong("capacity") ?: 0).toInt(),
                        name = doc.getString("name") ?: "",
                        owner = doc.getString("owner") ?: "",
                        description = doc.getString("description") ?: "",
                        rules = doc.getString("rules") ?: "",
                        location = doc.getString("location") ?: "",
                        availableStartDate =
                            doc.getLong("availableStartDate"),
                        availableEndDate =
                            doc.getLong("availableEndDate")
                    )
                }

                onResult(campsites)
            }
            .addOnFailureListener {
                onError(it.message ?: "Could not load campsites.")
            }
    }

    fun optNotification(enabled: Boolean): String {
        return if (enabled) {
            "User will receive notifications"
        } else {
            "User will not receive notifications"
        }
    }

    fun deleteProfile() {
        val currentUid =
            FirebaseAuth.getInstance().currentUser?.uid

        if (currentUid != uid) {
            Log.e("User", "Cannot delete another user's profile")
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .delete()
            .addOnFailureListener {
                Log.e("User", "deleteProfile failed", it)
            }
    }
}