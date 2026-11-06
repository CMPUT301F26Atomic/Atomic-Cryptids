package com.example.myapplication

import android.util.Log
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


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
}