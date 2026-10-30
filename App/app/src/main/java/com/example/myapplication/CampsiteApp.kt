package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atomiccryptids.campsiteapp.User

@Composable
fun CampsiteApp() {
    val navController = rememberNavController()

    var user by remember { mutableStateOf<User?>(null) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(user, navController) }
        composable("login") {

            LoginScreen(
                onSelectOwner = {
                    user = CampsiteOwner()
                    (user as CampsiteOwner).saveUser()
                },
                onSelectCamper = {
                    user = Camper()
                    (user as Camper).saveUser()
                },
                navController
            )
        }
        composable("camper") {
            CamperScreen(
                user as Camper,
                navController
            )
        }
        composable("owner") {

            OwnerScreen(
                user as CampsiteOwner,
                navController
            )
        }
    }
}