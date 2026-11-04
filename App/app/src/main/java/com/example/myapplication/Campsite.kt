package com.atomiccryptids.campsiteapp

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore

class Campsite(
    var id: String = "",
    private val name: String,
    private val capacity: Int,
    private val owner: String,
    private val description: String,
    private val location: String,
    private var occupancy: Int,
) {
    val campsiteToMap = mapOf(
        "capacity" to getCapacity(),
        "name" to getName(),
        "owner" to getOwner(),
        "description" to getDescription(),
        "location" to getLocation(),
        "occupancy" to getOccupancy()
    )

    private val participants = mutableStateListOf<String>()

    fun generateQRCode(): String {
        return "QR Code"
    }

    fun addParticipant(name: String) {
        if (participants.size < capacity) {
            participants.add(name)
        }
    }

    fun getCapacity(): Int {
        return capacity
    }

    fun getName(): String {
        return name
    }

    fun getOwner(): String {
        return owner
    }

    fun getDescription(): String {
        return description
    }

    fun getLocation(): String {
        return location
    }

    fun getOccupancy(): Int {
        return occupancy
    }

    fun save(onResult: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").add(campsiteToMap)
            .addOnSuccessListener { onResult("Saved") }
            .addOnFailureListener { onResult("Error: ${it.message}") }
    }
}