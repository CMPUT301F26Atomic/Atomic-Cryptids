package com.example.myapplication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source

class FirebaseAccountRepository : AccountRepository {
    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()

    private fun withUid(onError: (String) -> Unit, action: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            action(uid)
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { result ->
                    val signedInUid = result.user?.uid
                    if (signedInUid == null) onError("Firebase did not return a UID.")
                    else action(signedInUid)
                }
                .addOnFailureListener { onError(it.message ?: "Could not initialize device identity.") }
        }
    }

    override fun login(username: String, onSuccess: (AccountProfile) -> Unit, onError: (String) -> Unit) {
        try { AccountPolicy.username(username) } catch (error: IllegalArgumentException) {
            onError(error.message.orEmpty())
            return
        }
        withUid(onError) { uid ->
            db.collection("users").document(uid).get(Source.SERVER)
                .addOnSuccessListener { document ->
                    val profile = try {
                        AccountPolicy.login(uid, username, document.data)
                    } catch (error: Exception) {
                        onError(error.message ?: "Could not read the account.")
                        return@addOnSuccessListener
                    }
                    if (auth.currentUser?.uid == uid) onSuccess(profile)
                    else onError("The Firebase session changed. Please try again.")
                }
                .addOnFailureListener { onError(it.message ?: "Could not check the account. Check your connection.") }
        }
    }

    override fun register(username: String, role: String, onSuccess: (AccountProfile) -> Unit, onError: (String) -> Unit) {
        val key = try {
            AccountPolicy.route(role)
            AccountPolicy.usernameKey(username)
        } catch (error: IllegalArgumentException) {
            onError(error.message.orEmpty())
            return
        }
        withUid(onError) { uid ->
            val userRef = db.collection("users").document(uid)
            val nameRef = db.collection("usernames").document(key)
            db.runTransaction { transaction ->
                // Read both documents before writing. Competing registrations are retried.
                val existing = transaction.get(userRef)
                val reservedName = transaction.get(nameRef)
                require(!reservedName.exists() || reservedName.getString("uid") != null) {
                    "This username record is invalid. Contact the project administrator."
                }
                val profile = AccountPolicy.registration(uid, username, role,
                    existing.data, reservedName.getString("uid"))
                transaction.set(userRef, mapOf(
                    "uid" to uid,
                    "username" to profile.username,
                    "usernameKey" to key,
                    "role" to profile.role
                ), SetOptions.merge())
                transaction.set(nameRef, mapOf("uid" to uid))
                profile
            }.addOnSuccessListener { profile ->
                if (auth.currentUser?.uid == uid) onSuccess(profile)
                else onError("The Firebase session changed. Please try again.")
            }.addOnFailureListener { onError(it.message ?: "Could not create the account.") }
        }
    }
}
