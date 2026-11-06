package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User

@Composable
fun CampsiteApp() {
    val navController = rememberNavController()


    var user by rememberSaveable { mutableStateOf<User?>(null) }
    var campsite by rememberSaveable { mutableStateOf<Campsite?>(null) }
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(user, navController) }
        composable("login") {

            LoginScreen(
                navController,
                onSelectCamper = {
                    user = Camper()
                    (user as Camper).saveUser()
                },
                onSelectOwner = {
                    user = CampsiteOwner()
                    (user as CampsiteOwner).saveUser()
                },
                onSelectAdmin = {
                    user = Admin()
                    (user as Admin).saveUser()
                }
            )
        }
        composable("camper") {
            CamperScreen(
                navController,
                user as Camper,
                onSelectCampsite = { selectedCampsite -> campsite = selectedCampsite }
            )
        }
        composable("owner") {
            OwnerScreen(
                navController,
                user as CampsiteOwner
            )
        }
        composable("admin") {
            AdminScreen(
                navController,
                user as Admin,
                onSelectCampsite = { selectedCampsite -> campsite = selectedCampsite }
            )
        }
        composable("campsite") {
            CampsiteScreen(
                navController,
                campsite as Campsite,
                user as User
            )
        }
        composable("lottery") {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            LotteryScreen(
                ownerUid = uid,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
