package com.example.myapplication

import android.util.Log
import androidx.core.app.NotificationCompat
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Camper(
    var id:String = "",
    private var name:String = "",
    private var email:String = "",
    private var phone:String = ""
): User {
    override val role = "camper"

    fun setName(newName: String) {
        name = newName
    }

    fun setEmail(newEmail: String) {
        email = newEmail
    }

    fun setPhone(newPhone:String) {
        phone = newPhone
    }

    fun getName(): String {
        return name
    }

    fun getEmail(): String {
        return email
    }

    fun getPhone(): String {
        return phone
    }

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
        id = uid

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(mapOf("role" to role), SetOptions.merge())
            .addOnFailureListener { Log.e("User", "saveUser failed", it) }
    }

    /**
     * Saves the camper's personal information to Firestore without
     * modifying their existing account data or preferences.
     *
     * @param onResult Callback that receives a success message if the
     * information is saved successfully, or an error message if the
     * operation fails.
     */
    fun savePersonalInfo(onResult: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            onResult("Error: User not authenticated")
            return
        }

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone
                ),
                SetOptions.merge()
            )
            .addOnSuccessListener {
                onResult("Personal information saved")
            }
            .addOnFailureListener {
                onResult("Error: ${it.message}")
            }
    }

    /**
     * Retrieves the camper's saved personal information from Firestore
     * and updates the corresponding attributes of the camper object.
     *
     * @param onResult Callback that receives a success message or an
     * error message if the information could not be retrieved.
     */
    fun loadPersonalInfo(onResult: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            onResult("Error: User not authenticated")
            return
        }

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    name = document.getString("name") ?: ""
                    email = document.getString("email") ?: ""
                    phone = document.getString("phone") ?: ""

                    onResult("Personal information loaded")
                } else {
                    onResult("Error: Profile not found")
                }
            }
            .addOnFailureListener {
                onResult("Error: ${it.message}")
            }
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

        // This block of code was written with help from Claude, prompted with:
        // "How do I find a Firestore document that has a certain value in one of its lists"
        // (followed the outline from joinWaitlist)
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

        // This block of code was written with help from Claude, prompted with:
        // "How do I find a Firestore document that has a certain value in one of its lists"
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

                val lotteryRef = lottery.reference
                db.runTransaction { transaction ->
                    val snap = transaction.get(lotteryRef)
                    val waitlist = snap.get("waitlist") as? List<*> ?: emptyList<String>()
                    val waitlistCapacity = snap.getLong("waitlistCapacity")

                    if (uid in waitlist) {
                        throw IllegalStateException("You are already on the waiting list")
                    }
                    if (waitlistCapacity != null && waitlist.size >= waitlistCapacity) {
                        throw IllegalStateException("Waiting list is full")
                    }

                    transaction.update(lotteryRef, "waitlist", FieldValue.arrayUnion(uid))
                    null
                }
                    .addOnSuccessListener {
                        onResult("Joined waiting list")
                    }
                    .addOnFailureListener { error ->
                        onResult(error.message ?: "Could not join waiting list")
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

        // This block of code was written with help from Claude, prompted with:
        // "How do I find a Firestore document that has a certain value in one of its lists"
        // (followed the outline from joinWaitlist)
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

    /**
     * Camper adds a review for a campsite
     *
     * @param campsite campsite to be reviewed
     * @param rating five star rating scale
     * @param reviewText Text content of review
     */
    fun submitReview(campsite: Campsite, rating: Int, reviewText: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val review = mapOf("user_id" to uid, "campsite_id" to campsite.id, "rating" to rating, "text" to reviewText)

        FirebaseFirestore.getInstance().collection("reviews").add(review)
    }

    /**
     * get the messages that were sent to the user from the database
     *
     * @param onResult get the result of the search in a list of message objects
     */
    fun getMessages(onResult: (List<Message>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser

        FirebaseFirestore.getInstance().collection("messages").whereEqualTo("camperId", uid)
            .get()
            .addOnSuccessListener { result ->
                val message = result.documents.map { doc ->
                    Message(doc.id, doc.getString("camperId") ?: "", doc.getString("message") ?: "")
                }
                onResult(message)
            }
    }
}