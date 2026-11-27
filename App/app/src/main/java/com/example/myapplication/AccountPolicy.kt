package com.example.myapplication

import java.util.Locale

data class AccountProfile(val uid: String, val username: String, val role: String) {
    val route: String get() = AccountPolicy.route(role)
}

object AccountPolicy {
    val roles = listOf("camper", "campsite owner", "admin")

    fun username(value: String): String {
        val trimmed = value.trim()
        require(trimmed.matches(Regex("[A-Za-z0-9_.-]{3,30}"))) {
            "Use 3–30 letters, numbers, dots, underscores or hyphens."
        }
        return trimmed
    }

    fun usernameKey(value: String): String = username(value).lowercase(Locale.ROOT)

    fun route(role: String): String = when (role) {
        "camper" -> "camper"
        "campsite owner" -> "owner"
        "admin" -> "admin"
        else -> throw IllegalArgumentException("This account has an unknown role.")
    }

    fun login(uid: String, enteredName: String, saved: Map<String, Any>?): AccountProfile {
        require(saved != null) { "No account is registered for this UID. Choose Create account." }
        val name = saved["username"] as? String
        require(!name.isNullOrBlank()) { "This profile has no username. It must be reset before registering again." }
        require(usernameKey(name) == usernameKey(enteredName)) {
            "That username does not match the account registered on this device."
        }
        require(saved["uid"] == uid) { "The saved UID does not match this session." }
        val role = saved["role"] as? String ?: error("This account has no saved role.")
        route(role)
        return AccountProfile(uid, name, role)
    }

    fun registration(
        uid: String,
        enteredName: String,
        role: String,
        existing: Map<String, Any>?,
        usernameOwnerUid: String?
    ): AccountProfile {
        val name = username(enteredName)
        route(role)
        require(existing == null) {
            "This UID already has an account. Use Login."
        }
        require(usernameOwnerUid == null || usernameOwnerUid == uid) { "That username is already registered." }
        return AccountProfile(uid, name, role)
    }
}
