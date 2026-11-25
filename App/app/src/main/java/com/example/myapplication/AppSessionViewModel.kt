package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.atomiccryptids.campsiteapp.Campsite

class AppSessionViewModel : ViewModel() {

    val accounts = AccountController(
        repository = FirebaseAccountRepository()
    )

    var campsite by mutableStateOf<Campsite?>(null)

    fun logout() {
        campsite = null
        accounts.logout()
    }
}