package com.example.myapplication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AccountState(
    val busy: Boolean = false,
    val error: String? = null,
    val profile: AccountProfile? = null
)

/** Controller for the asynchronous registration/login flow. */
class AccountController(private val repository: AccountRepository) {
    private val mutableState = MutableStateFlow(AccountState())
    val state: StateFlow<AccountState> = mutableState.asStateFlow()
    private var requestNumber = 0

    fun login(username: String) = submit { success, failure -> repository.login(username, success, failure) }

    fun register(username: String, role: String) = submit { success, failure ->
        repository.register(username, role, success, failure)
    }

    fun clearError() {
        mutableState.value = mutableState.value.copy(error = null)
    }

    fun logout() {
        requestNumber++
        mutableState.value = AccountState()
        // Keep Firebase's anonymous UID so this account can log in again.
    }

    private fun submit(action: ((AccountProfile) -> Unit, (String) -> Unit) -> Unit) {
        if (mutableState.value.busy) return
        val request = ++requestNumber
        mutableState.value = AccountState(busy = true)
        action(
            { profile -> if (request == requestNumber) mutableState.value = AccountState(profile = profile) },
            { message -> if (request == requestNumber) mutableState.value = AccountState(error = message) }
        )
    }
}
