package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(user: User?, navController: NavController) {
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
    }

    //BackHandler(enabled = user != null) { user = null }

    when (user) {
        null -> navController.navigate("login") { launchSingleTop = true }
        is Camper -> navController.navigate("camper") { launchSingleTop = true }
        is CampsiteOwner -> navController.navigate("owner") { launchSingleTop = true }
        else -> {}
    }
}