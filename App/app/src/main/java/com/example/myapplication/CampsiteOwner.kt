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
        return Campsite(capacity, name, "owner", description, location, 0)
    }

    fun postCampsite(campsite: Campsite, onResult: (String) -> Unit) {
        campsite.save(onResult)
    }
}