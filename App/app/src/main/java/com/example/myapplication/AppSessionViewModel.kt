package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.atomiccryptids.campsiteapp.Campsite

/** Retains the MVC controller and selected campsite across screen rotation. */
class AppSessionViewModel : ViewModel() {
    val accounts = AccountController(FirebaseAccountRepository())
    var campsite by mutableStateOf<Campsite?>(null)

    var selectedCamper by mutableStateOf<Camper?>(null)

    fun logout() {
        selectedCamper = null
        campsite = null
        accounts.logout()
    }
}
