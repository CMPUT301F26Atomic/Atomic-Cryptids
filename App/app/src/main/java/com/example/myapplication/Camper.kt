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
    //AI
    fun searchCampsites(onResult: (List<Campsite>) -> Unit, onError: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("campsites").get()
            .addOnSuccessListener { result ->
                val campsites = result.documents.map { doc ->
                    Campsite(
                        capacity = (doc.getLong("capacity") ?: 0).toInt(),
                        name = doc.getString("name") ?: "",
                        owner = doc.getString("owner") ?: "",
                        description = doc.getString("description") ?: "",
                        location = doc.getString("location") ?: "",
                        occupancy = (doc.getLong("occupancy") ?: 0).toInt()
                    )
                }
                onResult(campsites)
            }
            .addOnFailureListener {
                onError("Error: ${it.message}")
            }
    }

    fun joinLottery(campsite: Campsite) {
        if (userLotteries.none { it.name == campsite.name && it.location == campsite.location }) {
            userLotteries.add(campsite)
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(mapOf("lotteries" to FieldValue.arrayUnion(campsite.toMap())), SetOptions.merge())
                .addOnFailureListener { Log.e("User", "joinLottery failed", it) }
        }
    }
}