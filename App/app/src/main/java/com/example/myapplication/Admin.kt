package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite
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
                    Camper()
                }
                onResult(campers)
            }
            .addOnFailureListener {
                onError("Error: ${it.message}")
            }

    }

    /**
     * Allows admin to delete a campsite from the database
     * Uses the id of the campsite to access it from the database
     *
     * @param campsite the campsite object which contains the information describing the campsite to be deleted
     */
    fun removeCampsite(campsite: Campsite) {
        FirebaseFirestore.getInstance().collection("campsites").document(campsite.id).delete()
    }
}