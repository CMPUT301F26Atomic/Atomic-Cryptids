package com.atomiccryptids.campsiteapp

import com.google.firebase.firestore.FirebaseFirestore

/**
 * A booking of one campsite by one user for a date range.
 *
 * Purpose: the shared model for reservation user stories
 *
 * Design rationale: an immutable data class, state changes produce a copy,
 * so a reservation held by the UI can never be mutated behind its back.
 * Date ranges are half-open i.e. start is included but end date is excluded,
 * so one user's checkout day can be the next user's check-in day.
 * A campsite is identified uniquely by (name + location).
 *
 * @param id Firestore document id
 * @param campsiteName name of the reserved campsite
 * @param campsiteLocation location of the reserved campsite
 * @param userId Firebase uid of the user
 * @param startDateMillis first reserved day
 * @param endDateMillis day the reservation ends
 * @param status lifecycle state
 */
data class Reservation(
    val id: String = "",
    val campsiteName: String,
    val campsiteLocation: String,
    val userId: String,
    val startDateMillis: Long,
    val endDateMillis: Long,
    val status: Status = Status.ACTIVE,
) {

    enum class Status { ACTIVE, CANCELLED, COMPLETED }

    init {
        require(endDateMillis > startDateMillis) { "Reservation must end after it starts" }
        require(userId.isNotBlank()) { "Reservation needs a userId" }
    }

    fun isActive(): Boolean = status == Status.ACTIVE

    fun overlaps(other: Reservation): Boolean =
        campsiteName == other.campsiteName &&
            campsiteLocation == other.campsiteLocation &&
            isActive() && other.isActive() &&
            startDateMillis < other.endDateMillis && other.startDateMillis < endDateMillis

    fun toMap(): Map<String, Any> = mapOf(
        "campsiteName" to campsiteName,
        "campsiteLocation" to campsiteLocation,
        "userId" to userId,
        "startDateMillis" to startDateMillis,
        "endDateMillis" to endDateMillis,
        "status" to status.name,
    )

    fun save(onResult: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION).add(toMap())
            .addOnSuccessListener { onResult("Saved") }
            .addOnFailureListener { onResult("Error: ${it.message}") }
    }

    fun cancel(onResult: (String) -> Unit) {
        if (id.isBlank()) return onResult("Error: reservation was never saved")
        FirebaseFirestore.getInstance().collection(COLLECTION).document(id)
            .update("status", Status.CANCELLED.name)
            .addOnSuccessListener { onResult("Cancelled") }
            .addOnFailureListener { onResult("Error: ${it.message}") }
    }

    companion object {
        const val COLLECTION = "reservations"

        /** Rebuilds a reservation from Firestore data */
        fun fromMap(id: String, data: Map<String, Any?>): Reservation? = runCatching {
            Reservation(
                id = id,
                campsiteName = data["campsiteName"] as? String ?: "",
                campsiteLocation = data["campsiteLocation"] as? String ?: "",
                userId = data["userId"] as String,
                startDateMillis = data["startDateMillis"] as Long,
                endDateMillis = data["endDateMillis"] as Long,
                status = Status.valueOf(data["status"] as String),
            )
        }.getOrNull()

        fun fetchActiveForUser(
            userId: String,
            onResult: (List<Reservation>) -> Unit,
            onError: (String) -> Unit,
        ) {
            FirebaseFirestore.getInstance().collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", Status.ACTIVE.name)
                .get()
                .addOnSuccessListener { result ->
                    val reservations = result.documents
                        .mapNotNull { fromMap(it.id, it.data ?: return@mapNotNull null) }
                        .sortedByDescending { it.startDateMillis }
                    onResult(reservations)
                }
                .addOnFailureListener { onError("Error: ${it.message}") }
        }
    }
}
