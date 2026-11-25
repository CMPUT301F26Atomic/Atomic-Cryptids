package com.example.myapplication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AccountState(
    val busy: Boolean = false,
    val error: String? = null,
    val profile: AccountProfile? = null
)

class AccountController(
    private val repository: AccountRepository
) {
    private val mutableState = MutableStateFlow(AccountState())

    val state: StateFlow<AccountState> =
        mutableState.asStateFlow()

    private var requestNumber = 0

    fun login(username: String) {
        submit { onSuccess, onError ->
            repository.login(
                username = username,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun register(
        username: String,
        role: String
    ) {
        submit { onSuccess, onError ->
            repository.register(
                username = username,
                role = role,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun clearError() {
        mutableState.value = mutableState.value.copy(
            error = null
        )
    }

    fun logout() {
        // Ignore callbacks from any previous request.
        requestNumber++

        mutableState.value = AccountState()

        // Keep Firebase's anonymous session.
        // Calling FirebaseAuth.signOut() would lose access
        // to the current anonymous identity.
    }

    private fun submit(
        action: (
            onSuccess: (AccountProfile) -> Unit,
            onError: (String) -> Unit
        ) -> Unit
    ) {
        // Prevent repeated taps from starting duplicate requests.
        if (mutableState.value.busy) return

        val currentRequest = ++requestNumber

        mutableState.value = AccountState(
            busy = true
        )

        val onSuccess: (AccountProfile) -> Unit = { profile ->
            if (currentRequest == requestNumber) {
                mutableState.value = AccountState(
                    profile = profile
                )
            }
        }

        val onError: (String) -> Unit = { message ->
            if (currentRequest == requestNumber) {
                mutableState.value = AccountState(
                    error = message
                )
            }
        }

        try {
            action(onSuccess, onError)
        } catch (error: Exception) {
            onError(
                error.message ?: "Could not complete the request."
            )
        }
    }
}