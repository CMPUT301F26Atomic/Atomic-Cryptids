package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.atomiccryptids.campsiteapp.User
import com.google.firebase.auth.FirebaseAuth

@Composable

fun HomeScreen(user: User?, navController: NavHostController) {
    LaunchedEffect(user?.role) {
        navController.navigate(user?.role?.let(AccountPolicy::route) ?: "login") {
            popUpTo("home") { inclusive = true }
            launchSingleTop = true
        }
    }
}
