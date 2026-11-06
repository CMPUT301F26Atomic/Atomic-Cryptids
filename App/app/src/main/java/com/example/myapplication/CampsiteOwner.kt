package com.example.myapplication

import android.util.Log
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


/*
Campsite Owner overrides interface methods, allowing for saving and deleting profile.
todo optNotification method is not connected to firebase yet

 */
class CampsiteOwner : User {
    override val role= "campsite owner"
    override fun optNotification (boolean: Boolean): String {
        if (boolean) {
            return "user will receive notification"
        }else { return " user wont receive notifications"}
    }


    /**
     * Creates a campsite with the specified details and availability dates.
     *
     * Associates the campsite with the currently authenticated Firebase user.
     * The availability start date is inclusive and the end date is exclusive.
     *
     * @param name Name of the campsite.
     * @param description Description of the campsite.
     * @param location Location of the campsite.
     * @param capacity Maximum number of guests.
     * @param rules Rules associated with the campsite.
     * @param availableStartDate First available date in UTC milliseconds.
     * @param availableEndDate Exclusive end date in UTC milliseconds.
     * @return The newly created Campsite object.
     */
    fun createCampsite(
        name: String,
        description: String,
        location: String,
        capacity: Int,
        rules: String,
        availableStartDate: Long,
        availableEndDate: Long
    ): Campsite {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return Campsite(
            capacity = capacity,
            name = name,
            owner = uid,
            description = description,
            location = location,
            rules = rules,
            availableStartDate = availableStartDate,
            availableEndDate = availableEndDate
        )
    }


    fun addPhoto (){
        //todo: when function is called and picture is given an image will be added to the campsite (linked via location possibly)
    }
    fun postCampsite(campsite: Campsite, onResult: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").add(campsite.campsiteToMap)
            .addOnSuccessListener { documentReference ->
                campsite.id = documentReference.id
                onResult("Saved")
            }
            .addOnFailureListener { onResult("Error: ${it.message}") }
    }
    /**
     * The following function was provided by OpenAI, ChatGPT when prompted with
     * "How do I retrieve a list of the sites belonging to a specific site owner?"
     *
     * Retrieves all campsites belonging to the authenticated campsite owner.
     *
     * @param onResult Callback receiving the owner's campsites.
     * @param onError Callback receiving an error message if retrieval fails.
     */
    fun getOwnedCampsites(
        onResult: (List<Campsite>) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            onError("No authenticated user found.")
            return
        }

        FirebaseFirestore.getInstance()
            .collection("campsites")
            .whereEqualTo("owner", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val campsites = snapshot.documents.map { doc ->
                    Campsite(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        capacity = (doc.getLong("capacity") ?: 0).toInt(),
                        owner = doc.getString("owner") ?: "",
                        description = doc.getString("description") ?: "",
                        location = doc.getString("location") ?: "",
                        rules = doc.getString("rules") ?: "",
                        availableStartDate = doc.getLong("availableStartDate"),
                        availableEndDate = doc.getLong("availableEndDate")
                    )
                }

                onResult(campsites)
            }
            .addOnFailureListener { exception ->
                onError("Failed to load campsites: ${exception.message}")
            }
    }

    /**
     * The following function was provided by OpenAI, ChatGPT when prompted with
     * "How do we make it so that a campsite can be updated by its owner"
     *
     * Updates an existing campsite in Firestore.
     *
     * Only allows the authenticated owner of the campsite to update it.
     *
     * @param campsiteId Firestore document ID of the campsite.
     * @param updatedFields Map containing the campsite fields to update.
     * @param onResult Callback receiving the result of the operation.
     */
    fun updateCampsite(
        campsiteId: String,
        updatedFields: Map<String, Any>,
        onResult: (String) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            onResult("Error: User is not authenticated.")
            return
        }

        val document = FirebaseFirestore.getInstance()
            .collection("campsites")
            .document(campsiteId)

        document.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    onResult("Error: Campsite not found.")
                    return@addOnSuccessListener
                }

                if (snapshot.getString("owner") != uid) {
                    onResult("Error: You do not own this campsite.")
                    return@addOnSuccessListener
                }

                document.update(updatedFields)
                    .addOnSuccessListener {
                        onResult("Updated")
                    }
                    .addOnFailureListener {
                        onResult("Error: ${it.message}")
                    }
            }
            .addOnFailureListener {
                onResult("Error: ${it.message}")
            }
    }

    /**
     * Creates new lottery for specific campsite and saves to firebase
     *
     * @param campsiteId campsiteId is the firebase id of the selected campsite
     * @param numberOfWinners number of winners for the lottery
     * @param onResult passes success string to UI when firebase operation is complete
     */
    fun createLottery(campsiteId: String, numberOfWinners: Int, onResult: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return // if user is not logged in return
        val lotteryData = mapOf("campsiteIds" to listOf(campsiteId), "numberOfWinners" to numberOfWinners, "ownerId" to uid, "waitlist" to emptyList<String>()) //store list of campsiteIds and initilize empty waitlist
        FirebaseFirestore.getInstance().collection("lotteries").add(lotteryData)
            .addOnSuccessListener { onResult("Lottery Created!") }
            .addOnFailureListener { Log.e("Lottery", "Failed", it) }
    }

    fun getWaitlist(campsite: Campsite, onResult: (List<String>) -> Unit, onError: (String) -> Unit) {

        // This block of code was written with help from Claude, prompted with:
        // "How do I find a Firestore document that has a certain value in one of its lists"
        // (followed the outline from joinWaitlist)
        val db = FirebaseFirestore.getInstance()
        db.collection("lotteries")
            .whereArrayContains("campsiteIds", campsite.id)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError("This campsite has no lottery")
                    return@addOnSuccessListener
                }

                val lottery = result.documents[0]
                val waitlist = lottery.get("waitlist") as? List<*> ?: emptyList<String>()
                onResult(waitlist.map { it.toString() })
            }
            .addOnFailureListener { error ->
                onError("Could not find lottery: " + error.message)
            }
    }
}