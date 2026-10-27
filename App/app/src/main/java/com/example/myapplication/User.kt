package com.atomiccryptids.campsiteapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

open class User {
    var isAdmin: Boolean = false
        private set
    var isCamper: Boolean = false
        private set
    var isCampsiteOwner: Boolean = false
        private set

    fun makeAdmin() {
        isAdmin = true
    }

    fun makeCamper() {
        isCamper = true
    }

    fun makeCampsiteOwner() {
        isCampsiteOwner = true
    }

    fun saveUser() {
        FirebaseFirestore.getInstance().collection("users").add(
            mapOf(
                "isAdmin" to isAdmin,
                "isCamper" to isCamper,
                "isCampsiteOwner" to isCampsiteOwner
            )
        ).addOnFailureListener {
            Log.e("Campsite", "saveUser failed", it)
        }
    }

    fun optNotification() {
        println("He will receive notifications")
    }

    fun deleteProfile() {
        TODO()
    }
}

class Camper : User() {
    fun searchCampsites(onResult: (List<Campsite>) -> Unit, onError: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").get()
            .addOnSuccessListener { result ->
                val campsites = result.documents.map { doc ->
                    Campsite(
                        capacity = (doc.getLong("capacity") ?: 0).toInt(),
                        name = doc.getString("name") ?: "",
                        owner = doc.getString("owner") ?: "",
                        description = doc.getString("description") ?: "",
                        location = doc.getString("location") ?: "",
                        occupancy = (doc.getLong("occupancy") ?: 0).toInt()
                    )
                }
                onResult(campsites)
            }
            .addOnFailureListener {
                onError("Error: ${it.message}")
            }
    }
}

class CampsiteOwner : User() {
    fun createCampsite(name: String, description: String, location: String, capacity: Int): Campsite {
        return Campsite(capacity, name, "owner", description, location, 0)
    }

    fun postCampsite(campsite: Campsite, onResult: (String) -> Unit) {
        campsite.save(onResult)
    }
}