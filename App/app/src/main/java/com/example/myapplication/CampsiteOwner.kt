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
    override fun saveUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(mapOf("role" to role), SetOptions.merge())
            .addOnFailureListener { Log.e("User", "saveUser failed", it) }
    }

    override fun deleteProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .delete()
            .addOnFailureListener { Log.e("User", "deleteProfile failed", it) }
    }

    fun createCampsite(name: String, description: String, location: String, capacity: Int): Campsite {
        return Campsite(capacity = capacity, name = name, owner = "owner", description = description, location = location, occupancy = 0)
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