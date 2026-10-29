package com.example.myapplication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.CamperScreen
import com.atomiccryptids.campsiteapp.OwnerScreen
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CampsiteApp(modifier: Modifier = Modifier) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
    }

    BackHandler(enabled = user != null) { user = null }

    Column(modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        when (val u = user) {
            null -> LoginScreen { user = it }
            is Camper -> CamperScreen(u) { user = null }
            is CampsiteOwner -> OwnerScreen(u) { user = null }
            else -> {}
        }
    }
}