package com.atomiccryptids.campsiteapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue

interface User {
    val role: String

    fun saveUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(mapOf("role" to role), SetOptions.merge())
            .addOnFailureListener { Log.e("User", "saveUser failed", it) }
    }

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
    fun searchCampsites(onResult: (List<Campsite>) -> Unit, onError: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").get()
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
                        availableStartDate = doc.getLong("availableStartDate"),
                        availableEndDate = doc.getLong("availableEndDate")
                    )
                }
                onResult(campsites)
            }
            .addOnFailureListener {
                onError("Error: ${it.message}")
            }
    }

    fun optNotification(boolean: Boolean): String {
        return if (boolean) "user will receive notification" else "user won't receive notifications"
        /*
        I am too tired and its late. I'll fix this part later
         */
    }
    fun deleteProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .delete()
            .addOnFailureListener { Log.e("User", "deleteProfile failed", it) }
    }
}
