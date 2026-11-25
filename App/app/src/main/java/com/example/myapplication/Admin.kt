package com.example.myapplication

import com.atomiccryptids.campsiteapp.User
import com.google.firebase.firestore.FirebaseFirestore

class Admin(override val uid: String,override val username: String) :
    User {
    override val role: String = "admin"

    fun getCampers(
        onResult: (List<Camper>) -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("role", "camper")
            .get()
            .addOnSuccessListener { result ->
                val campers = try {
                    result.documents.map { doc ->
                        val savedUid = requireNotNull(
                            doc.getString("uid")
                        ) {
                            "A camper profile is missing its UID."
                        }

                        val savedUsername = requireNotNull(
                            doc.getString("username")
                        ) {
                            "A camper profile is missing its username."
                        }

                        require(savedUid == doc.id) {
                            "A camper profile has an incorrect UID."
                        }

                        Camper(
                            uid = savedUid,
                            username = savedUsername
                        )
                    }
                } catch (error: IllegalArgumentException) {
                    onError(
                        error.message ?: "Could not read camper profiles."
                    )
                    return@addOnSuccessListener
                }

                onResult(campers)
            }
            .addOnFailureListener {
                onError(it.message ?: "Could not load campers.")
            }
    }
}