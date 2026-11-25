package com.example.myapplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source

class FirebaseAccountRepository : AccountRepository {

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    private fun withUid(
        onError: (String) -> Unit,
        action: (String) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid

        if (currentUid != null) {
            action(currentUid)
            return
        }

        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                val uid = result.user?.uid

                if (uid == null) {
                    onError("Firebase did not return a UID.")
                } else {
                    action(uid)
                }
            }
            .addOnFailureListener { error ->
                onError(
                    error.message
                        ?: "Could not initialize your Firebase account."
                )
            }
    }

    override fun login(
        username: String,
        onSuccess: (AccountProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val enteredName = try {
            AccountPolicy.username(username)
        } catch (error: IllegalArgumentException) {
            onError(error.message ?: "Invalid username.")
            return
        }

        withUid(onError) { uid ->
            db.collection("users")
                .document(uid)
                .get(Source.SERVER)
                .addOnSuccessListener { document ->
                    val profile = try {
                        AccountPolicy.login(
                            uid = uid,
                            enteredName = enteredName,
                            saved = document.data
                        )
                    } catch (error: Exception) {
                        onError(
                            error.message
                                ?: "Could not read your account."
                        )
                        return@addOnSuccessListener
                    }

                    if (auth.currentUser?.uid == uid) {
                        onSuccess(profile)
                    } else {
                        onError(
                            "Your Firebase session changed. Please try again."
                        )
                    }
                }
                .addOnFailureListener { error ->
                    onError(
                        error.message
                            ?: "Could not log in. Check your connection."
                    )
                }
        }
    }

    override fun register(
        username: String,
        role: String,
        onSuccess: (AccountProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val name: String
        val key: String

        try {
            name = AccountPolicy.username(username)
            key = AccountPolicy.usernameKey(name)

            // Reject unrecognized roles.
            AccountPolicy.route(role)
        } catch (error: IllegalArgumentException) {
            onError(
                error.message ?: "Invalid username or role."
            )
            return
        }

        withUid(onError) { uid ->
            val userRef = db.collection("users").document(uid)
            val usernameRef = db.collection("usernames").document(key)

            db.runTransaction { transaction ->
                // Read both documents before writing anything.
                val existingUser = transaction.get(userRef)
                val existingUsername = transaction.get(usernameRef)

                require(
                    !existingUsername.exists() ||
                            existingUsername.getString("uid") != null
                ) {
                    "This username record is invalid."
                }

                val profile = AccountPolicy.registration(
                    uid = uid,
                    enteredName = name,
                    role = role,
                    existing = existingUser.data,
                    usernameOwnerUid =
                        existingUsername.getString("uid")
                )

                transaction.set(
                    userRef,
                    mapOf(
                        "uid" to profile.uid,
                        "username" to profile.username,
                        "usernameKey" to key,
                        "role" to profile.role
                    ),
                    SetOptions.merge()
                )

                // Reserve this username for the same UID.
                transaction.set(
                    usernameRef,
                    mapOf("uid" to uid)
                )

                profile
            }
                .addOnSuccessListener { profile ->
                    if (auth.currentUser?.uid == uid) {
                        onSuccess(profile)
                    } else {
                        onError(
                            "Your Firebase session changed. Please try again."
                        )
                    }
                }
                .addOnFailureListener { error ->
                    onError(
                        error.message
                            ?: "Could not create your account."
                    )
                }
        }
    }
}