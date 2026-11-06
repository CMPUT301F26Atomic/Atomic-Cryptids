package com.example.myapplication

import android.util.Log
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Camper: User {

    private val userLotteries: MutableList<Campsite> = mutableListOf()
    override val role = "camper"

    override fun optNotification (boolean: Boolean) : String {
        if (boolean) {
            return "user will receive notification"
        } else {
            return "no notifications"
        }
    }

    fun joinLottery(campsite: Campsite) {
        if (userLotteries.none { it.getName() == campsite.getName() && it.getLocation() == campsite.getLocation() }) {
            userLotteries.add(campsite)
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(mapOf("lotteries" to FieldValue.arrayUnion(campsite.campsiteToMap)), SetOptions.merge())
                .addOnFailureListener { Log.e("User", "joinLottery failed", it) }
        }
    }
}