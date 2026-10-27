package com.atomiccryptids.campsiteapp

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore

class Campsite(
    val capacity: Int,
    val name: String,
    private val owner: String,
    val description: String,
    val location: String,
    private var occupancy: Int
) {
    private val participants = mutableStateListOf<String>()

    fun save(onResult: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").add(
            mapOf(
                "capacity" to capacity,
                "name" to name,
                "owner" to owner,
                "description" to description,
                "location" to location,
                "occupancy" to occupancy
            )
        ).addOnSuccessListener {
            onResult("Saved")
        }.addOnFailureListener {
            onResult("Error: ${it.message}")
        }
    }

    fun generateQRCode() {
        println("QR Code")
    }

    fun addLottery(name: String) {
        if (participants.size < capacity) {
            participants.add(name)
        }
    }
}