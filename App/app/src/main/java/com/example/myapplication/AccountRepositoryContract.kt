package com.example.myapplication

interface AccountRepository {
    fun login(
        username: String,
        onSuccess: (AccountProfile) -> Unit,
        onError: (String) -> Unit)
    fun register(username: String,
                 role: String,
                 onSuccess: (AccountProfile) -> Unit,
                 onError: (String) -> Unit)
}

