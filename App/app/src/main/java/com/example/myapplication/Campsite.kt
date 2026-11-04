package com.atomiccryptids.campsiteapp

import androidx.compose.runtime.mutableStateListOf
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
    var id: String = "",
    private val name: String,
    private val capacity: Int,
    private val owner: String,
    private val occupancy: Int = 0,
    private val description: String,
    private val location: String,
    private val rules: String = "",
) {
    private val participants = mutableStateListOf<String>()

    val campsiteToMap: Map<String, Any>
        get() = mapOf(
            "capacity" to capacity,
            "name" to name,
            "owner" to owner,
            "description" to description,
            "location" to location,
            "rules" to rules,
            "occupancy" to participants.size
        )

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

    fun getRules(): String {
        return rules
    }

    fun getOccupancy(): Int {
        return participants.size
    }

    fun save(onResult: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").add(campsiteToMap)
            .addOnSuccessListener { onResult("Saved") }
            .addOnFailureListener { onResult("Error: ${it.message}") }
    }
}
