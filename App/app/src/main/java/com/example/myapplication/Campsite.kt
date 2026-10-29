package com.atomiccryptids.campsiteapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.myapplication.CampsiteOwner
import com.google.firebase.firestore.FirebaseFirestore

/*
attributes:
name, description, geolocation, max occupancy, amenities, rules

Responsibilities
availability and check available dates(todo)
Manage pricing
Manage images and tags
Maintain rating
be booked by camper
associations with lottery
associate with owner
 */

class Campsite(
    val name: String,
    val description: String,
    val location: String,
    val capacity: Int,
    val rules: String,
     val owner: CampsiteOwner,

) {
    private val participants = mutableStateListOf<String>()

    fun generateQRCode(): String {
        return "QR Code"
    }

    fun addParticipant(name: String) {
        if (participants.size < capacity) {
            participants.add(name)
        }
    }

    fun getCapacity(name: String): Int {
        return capacity
    }

    fun getCampsite(): Map<String, Any> = mapOf(
        "capacity" to capacity,
        "name" to name,
        "owner" to owner,
        "description" to description,
        "location" to location,
        "rules" to rules,
        "occupancy" to participants.size
    )

    fun save(onResult: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").add(getCampsite())
            .addOnSuccessListener { onResult("Saved") }
            .addOnFailureListener { onResult("Error: ${it.message}") }
    }

}