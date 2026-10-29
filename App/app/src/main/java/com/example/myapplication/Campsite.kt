package com.atomiccryptids.campsiteapp

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore

class Campsite(
    val capacity: Int,
    val name: String,
    private val owner: String,
    val description: String,
    val location: String,
    private var occupancy: Int,
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
    // in Campsite
    fun toMap(): Map<String, Any> = mapOf(
        "capacity" to capacity,
        "name" to name,
        "owner" to owner,
        "description" to description,
        "location" to location,
        "occupancy" to occupancy
    )

    fun save(onResult: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").add(toMap())
            .addOnSuccessListener { onResult("Saved") }
            .addOnFailureListener { onResult("Error: ${it.message}") }
    }
}