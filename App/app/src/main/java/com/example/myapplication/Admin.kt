package com.example.myapplication

import android.util.Log
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/*
Admin is an instance of interface User. Will be completed in the second sprint
 */
class Admin : User {
    override val role = "admin"

    /**
     * Gets all camper profiles from Firestore
     *
     * Converts each camper user to a camper object
     *
     * @param onResult Callback that receives the list of retrieved campsites.
     * @param onError Callback that receives an error message if retrieval fails.
     */
    fun getCampers(
        onResult: (List<Camper>) -> Unit,
        onError: (String) -> Unit,
    ) {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .whereEqualTo("role", "camper")
            .get()
            .addOnSuccessListener { result ->
                val campers =
                    result.documents.map { doc ->
                        Camper(
                            doc.id,
                            doc.getString("name") ?: "",
                            doc.getString("email") ?: "",
                            doc.getString("phone") ?: "",
                        )
                    }
                onResult(campers)
            }.addOnFailureListener {
                onError("Error: ${it.message}")
            }
    }

    /**
     * Removes a camper profile from Firestore using their unique ID.
     *
     * @param camper The camper whose profile will be removed.
     * @param onSuccess Callback invoked when deletion succeeds.
     * @param onError Callback that receives an error message if deletion fails.
     */
    fun removeCamper(
        camper: Camper,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(camper.id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("Admin", "removeCamper failed", it)
                onError("Error: ${it.message}")
            }
    }

    /**
     * Removes a campsite from Firestore using its unique ID.
     *
     * @param campsite The campsite to be removed.
     * @param onSuccess Callback invoked when deletion succeeds.
     * @param onError Callback that receives an error message if deletion fails.
     */
    fun removeCampsite(
        campsite: Campsite,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("campsites")
            .document(campsite.id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("Admin", "removeCampsite failed", it)
                onError("Error: ${it.message}")
            }
    }
}
