package com.example.myapplication
import java.util.Locale

data class AccountProfile(val uid: String,val username: String,val role: String) {
    val route: String
        get() = AccountPolicy.route(role)
}

object AccountPolicy {
    val roles = listOf(
        "camper",
        "campsite owner",
        "admin"
    )

    fun username(value: String): String {
        val name = value.trim()

        require(name.matches(Regex("[A-Za-z0-9_.-]{3,30}"))) {
            "Use 3–30 letters, numbers, dots, underscores or hyphens."
        }

        return name
    }

    fun usernameKey(value: String): String {
        return username(value).lowercase(Locale.ROOT)
    }

    fun route(role: String): String {
        return when (role) {
            "camper" -> "camper"
            "campsite owner" -> "owner"
            "admin" -> "admin"

            else -> throw IllegalArgumentException(
                "This account has an unknown role."
            )
        }
    }

    fun login(
        uid: String,
        enteredName: String,
        saved: Map<String, Any>?
    ): AccountProfile {
        require(saved != null) {
            "No account exists for this device. Create an account first."
        }

        val savedUid = saved["uid"] as? String
        val savedUsername = saved["username"] as? String
        val savedRole = saved["role"] as? String

        require(savedUid == uid) {
            "The saved account does not match this Firebase UID."
        }

        require(!savedUsername.isNullOrBlank()) {
            "This account is missing its username."
        }

        require(
            usernameKey(savedUsername) == usernameKey(enteredName)
        ) {
            "Incorrect username for this device."
        }

        require(savedRole != null) {
            "This account is missing its role."
        }

        route(savedRole)

        return AccountProfile(
            uid = uid,
            username = savedUsername,
            role = savedRole
        )
    }

    fun registration(
        uid: String,
        enteredName: String,
        role: String,
        existing: Map<String, Any>?,
        usernameOwnerUid: String?
    ): AccountProfile {
        require(uid.isNotBlank()) {
            "A Firebase UID is required."
        }

        val name = username(enteredName)

        route(role)

        require(existing == null) {
            "An account already exists for this device. Please log in."
        }

        require(
            usernameOwnerUid == null || usernameOwnerUid == uid
        ) {
            "That username is already taken."
        }

        return AccountProfile(
            uid = uid,
            username = name,
            role = role
        )
    }
}