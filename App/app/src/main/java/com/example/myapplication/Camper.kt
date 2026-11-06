package com.example.myapplication

import android.util.Log
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Camper: User {

    override val role = "camper"

    /**
     * @param boolean is the state of the user's optnotification setting.
     * If true, user will receive notifications and this information will be recorded in firebase.
     */

    override fun optNotification (boolean: Boolean) : String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(mapOf("notifications" to boolean), SetOptions.merge())
                .addOnFailureListener { Log.e("User", "optNotification failed", it) }
        }
        if (boolean) {
            return "user will receive notification"
        } else {
            return "no notifications"
        }
    }
    /**
    saves current users attributes to firebase under collection users.
    It records the role of the user.
    prints error message if save fails
     */
    override fun saveUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(mapOf("role" to role), SetOptions.merge())
            .addOnFailureListener { Log.e("User", "saveUser failed", it) }
    }
    /**
    deletes current users profile from firebase.
    prints error message if delete fails
    possible source of error: if camper and owner have same name
     */
    override fun deleteProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .delete()
            .addOnFailureListener { Log.e("User", "deleteProfile failed", it) }
    }


    /**
     * Retrieves all campsites from Firestore.
     *
     * Converts each campsite document into a Campsite object,
     * including its location, capacity, and availability dates.
     * Campsites are returned without filtering.
     *
     * @param onResult Callback that receives the list of retrieved campsites.
     * @param onError Callback that receives an error message if retrieval fails.
     */
    override fun searchCampsites(onResult: (List<Campsite>) -> Unit, onError: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").get()
            .addOnSuccessListener { result ->
                val campsites = result.documents.map { doc ->
                    Campsite(
                        id = doc.id,
                        capacity = (doc.getLong("capacity") ?: 0).toInt(),
                        name = doc.getString("name") ?: "",
                        owner = doc.getString("owner") ?: "",
                        description = doc.getString("description") ?: "",
                        rules = doc.getString("rules") ?: "",
                        location = doc.getString("location") ?: "",
                        availableStartDate = doc.getLong("availableStartDate"),
                        availableEndDate = doc.getLong("availableEndDate")
                    )
                }
                onResult(campsites)
            }
            .addOnFailureListener {
                onError("Error: ${it.message}")
            }
    }

    fun joinLottery(campsite: Campsite, onResult: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onResult("You are not signed in")
            return
        }
        val uid = user.uid

        val db = FirebaseFirestore.getInstance()
        db.collection("lotteries")
            .whereArrayContains("campsiteIds", campsite.id)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onResult("This campsite has no lottery")
                    return@addOnSuccessListener
                }

                val lottery = result.documents[0]

                if (!LotteryValidator.isRegistrationOpen(
                        System.currentTimeMillis(),
                        lottery.getLong("registrationStartDate"),
                        lottery.getLong("registrationEndDate"))
                ) {
                    onResult("Registration is closed for this lottery")
                    return@addOnSuccessListener
                }

                val capacity = lottery.getLong("capacity") ?: 0
                val entrants = lottery.get("entrants") as? List<*> ?: emptyList<String>()

                if (uid in entrants) {
                    onResult("You are already in the lottery")
                    return@addOnSuccessListener
                }

                if (entrants.size >= capacity) {
                    onResult("Lottery is full, join the waiting list")
                    return@addOnSuccessListener
                }

                lottery.reference.update("entrants", FieldValue.arrayUnion(uid))
                    .addOnSuccessListener {
                        onResult("Joined lottery")
                    }
                    .addOnFailureListener { error ->
                        onResult("Could not join: " + error.message)
                    }
            }
            .addOnFailureListener { error ->
                onResult("Could not find lottery: " + error.message)
            }
    }

    fun joinWaitlist(campsite: Campsite, onResult: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onResult("You are not signed in")
            return
        }
        val uid = user.uid

        val db = FirebaseFirestore.getInstance()
        db.collection("lotteries")
            .whereArrayContains("campsiteIds", campsite.id)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onResult("This campsite has no lottery")
                    return@addOnSuccessListener
                }

                val lottery = result.documents[0]

                if (!LotteryValidator.isRegistrationOpen(
                        System.currentTimeMillis(),
                        lottery.getLong("registrationStartDate"),
                        lottery.getLong("registrationEndDate"))
                ) {
                    onResult("Registration is closed for this lottery")
                    return@addOnSuccessListener
                }

                val capacity = lottery.getLong("capacity") ?: 0
                val entrants = lottery.get("entrants") as? List<*> ?: emptyList<String>()

                if (uid in entrants) {
                    onResult("Could not join waiting list, You are already in the lottery")
                    return@addOnSuccessListener
                }

                if (entrants.size < capacity) {
                    onResult("Lottery is not full, join the lottery instead")
                    return@addOnSuccessListener
                }

                lottery.reference.update("waitlist", FieldValue.arrayUnion(uid))
                    .addOnSuccessListener {
                        onResult("Joined waiting list")
                    }
                    .addOnFailureListener { error ->
                        onResult("Could not join: " + error.message)
                    }
            }
            .addOnFailureListener { error ->
                onResult("Could not find lottery: " + error.message)
            }
    }

    fun leaveWaitlist(campsite: Campsite, onResult: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onResult("You are not signed in")
            return
        }
        val uid = user.uid

        val db = FirebaseFirestore.getInstance()
        db.collection("lotteries")
            .whereArrayContains("campsiteIds", campsite.id)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onResult("This campsite has no lottery")
                    return@addOnSuccessListener
                }

                val lottery = result.documents[0]

                val waitlist = lottery.get("waitlist") as? List<*> ?: emptyList<String>()
                if (uid !in waitlist) {
                    onResult("You are not in the waiting list")
                    return@addOnSuccessListener
                }

                lottery.reference.update("waitlist", FieldValue.arrayRemove(uid))
                    .addOnSuccessListener {
                        onResult("Left waiting list")
                    }
                    .addOnFailureListener { error ->
                        onResult("Could not leave: " + error.message)
                    }
            }
            .addOnFailureListener { error ->
                onResult("Could not find lottery: " + error.message)
            }
    }
}