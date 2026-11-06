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

/**
 * Represents a campsite with all its attributes
 * list of participants keep track of how many people are booked
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
    private val availableStartDate: Long? = null,
    private val availableEndDate: Long? = null,
) {
    private val participants = mutableStateListOf<String>()

    val campsiteToMap: Map<String, Any?>
        get() = buildMap {
            put("capacity", capacity)
            put("name", name)
            put("owner", owner)
            put("description", description)
            put("location", location)
            put("rules", rules)
            put("occupancy", participants.size)

            availableStartDate?.let {
                put("availableStartDate", it)
            }

            availableEndDate?.let {
                put("availableEndDate", it)
            }
        }
            /**
             * Checks whether the campsite is available on a specified date.
             *
             * The start date is inclusive and the end date is exclusive.
             * Returns false if the campsite has no availability dates specified.
             *
             * @param date The date to check, in milliseconds.
             * @return True if the date is within the campsite's availability,
             */
            fun isAvailableOn(date: Long): Boolean {
                val start = availableStartDate ?: return false
                val end = availableEndDate ?: return false

                return date >= start && date < end
            }

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
