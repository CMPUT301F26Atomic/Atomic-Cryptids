package com.example.myapplication

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Holds the information for a notification that a camper received.
 *
 * @param message the text the camper will see
 * @param type "win" or "lose"
 * @param sentAt the time it was sent in milliseconds
 */
data class NotificationItem(
    val message: String,
    val type: String,
    val sentAt: Long
)

/**
 * Saves and loads notifications using the "notifications" collection in Firestore.
 */
object Notification {

    /**
     * Saves one notification for one camper.
     *
     * @param uid the uid of the camper who gets the notification
     * @param type "win" or "lose"
     * @param message the text the camper will see
     */
    fun send(uid: String, type: String, message: String) {
        val db = FirebaseFirestore.getInstance()

        val notificationData = hashMapOf(
            "uid" to uid,
            "type" to type,
            "message" to message,
            "sentAt" to System.currentTimeMillis()
        )

        db.collection("notifications")
            .add(notificationData)
    }

    /**
     * Loads all the notifications for one camper, with the newest one first.
     *
     * @param uid the uid of the camper
     * @param onResult called with the list of notifications
     * @param onError called with an error message if loading fails
     */
    fun getNotifications(uid: String, onResult: (List<NotificationItem>) -> Unit, onError: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("notifications")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                val notifications = mutableListOf<NotificationItem>()

                for (doc in result.documents) {
                    val message = doc.getString("message") ?: ""
                    val type = doc.getString("type") ?: ""
                    val sentAt = doc.getLong("sentAt") ?: 0L

                    val notification = NotificationItem(message, type, sentAt)
                    notifications.add(notification)
                }

                val sortedNotifications = notifications.sortedByDescending { notification -> notification.sentAt }
                onResult(sortedNotifications)
            }
            .addOnFailureListener { error ->
                onError("Could not load notifications: " + error.message)
            }
    }
}