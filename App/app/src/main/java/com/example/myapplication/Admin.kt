package com.example.myapplication

import android.util.Log
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/*
Admin is an instance of interface User. Will be completed in the second sprint
 */
class Admin: User {
    override val role = "admin"

    /**
     * Gets all camper profiles from Firestore
     *
     * Converts each camper user to a camper object
     *
     * @param onResult Callback that receives the list of retrieved campsites.
     * @param onError Callback that receives an error message if retrieval fails.
     */
    fun getCampers(onResult: (List<Camper>) -> Unit, onError: (String) -> Unit) {

        FirebaseFirestore.getInstance().collection("users").whereEqualTo("role", "camper").get()
            .addOnSuccessListener { result ->
                val campers = result.documents.map { doc ->
                    Camper(
                        doc.id,
                        doc.getString("name") ?: "",
                        doc.getString("email") ?: "",
                        doc.getString("phone") ?: "",
                        )
                }
                onResult(campers)
            }
            .addOnFailureListener {
                onError("Error: ${it.message}")
            }
    }

    /**
     * Removes a camper from the database
     *
     * @param camper camper object containing the id attribute used to query the database
     */
    fun removeCamper(camper: Camper) {
        FirebaseFirestore.getInstance().collection("users").document(camper.id)
            .delete()
            .addOnFailureListener { Log.e("User", "deleteProfile failed", it) }
    }
}