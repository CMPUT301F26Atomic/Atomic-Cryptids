package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User

@Composable
fun CampsiteApp(session: AppSessionViewModel) {
    val state by session.accounts.state.collectAsState()
    val profile = state.profile
    if (profile == null) {
        LoginScreen(state, session.accounts)
        return
    }
    val navController = rememberNavController()
    val user: User = remember(profile) {
        when (profile.role) {
            "camper" -> Camper(profile.uid, profile.username)
            "campsite owner" -> CampsiteOwner(profile.uid, profile.username)
            "admin" -> Admin(profile.uid, profile.username)
            else -> error("Unknown role: ${profile.role}")
        }
    }
    var campsite by session::campsite
    var camper by session::selectedCamper
    NavHost(navController = navController, startDestination = profile.route) {
        composable("home") { HomeScreen(user, navController) }
        composable("login") { LaunchedEffect(Unit) { session.logout() } }
        composable("camper") {
            if (user !is Camper) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            CamperScreen(
                navController,
                user as Camper,
                onSelectCampsite = { selectedCampsite -> campsite = selectedCampsite }
            )
        }
        composable("camper-profile") {
            if (camper == null || user !is Admin) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            CamperProfileScreen(
                navController,
                camper as Camper
            )
        }
        composable("owner-campsite-details") {
            if (campsite == null || user !is CampsiteOwner) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            OwnerCampsiteDetailsScreen(
                campsite = campsite as Campsite,
                onBack = {
                    navController.popBackStack()
                },
                onEdit = {
                    navController.navigate("edit-owner-campsite")
                },
                onWaitList = {
                    navController.navigate("owner-waitlist")
                }
            )
        }

        composable("owner-waitlist") {
            if (campsite == null || user !is CampsiteOwner) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            OwnerWaitlistScreen(
                campsite = campsite as Campsite,
                owner = user as CampsiteOwner,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("edit-owner-campsite") {
            if (campsite == null || user !is CampsiteOwner) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            EditOwnerCampsiteScreen(
                campsite = campsite as Campsite,
                owner = user as CampsiteOwner,
                onBack = {
                    navController.popBackStack()
                },
                onSaved = {
                    val selectedId = (campsite as Campsite).id

                    (user as CampsiteOwner).getOwnedCampsites(
                        onResult = { updatedCampsites ->
                            val updatedCampsite = updatedCampsites.firstOrNull {
                                it.id == selectedId
                            }

                            if (updatedCampsite != null) {
                                campsite = updatedCampsite
                            }

                            navController.popBackStack()
                        },
                        onError = {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }

        composable("owner") {
            if (user !is CampsiteOwner) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            OwnerScreen(
                navController = navController,
                owner = user as CampsiteOwner,
                onSelectCampsite = { selectedCampsite ->
                    campsite = selectedCampsite
                }
            )
        }
        composable("admin") {
            if (user !is Admin) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            AdminScreen(
                navController,
                user as Admin,
                onSelectCamper = { selectedCamper -> camper = selectedCamper },
                onSelectCampsite = { selectedCampsite -> campsite = selectedCampsite }
            )
        }
        composable("campsite") {
            if (campsite == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
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
        composable("wins") {
            LotteryWinsScreen(
                navController,
                onSelectCampsite = { selectedCampsite -> campsite = selectedCampsite }
            )
        }
        composable("reserve") {
            if (campsite == null || user !is Camper) {
                LaunchedEffect(Unit) {
                    navController.navigate(profile.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            ReserveScreen(
                navController,
                campsite as Campsite
            )
        }
        composable("myreservations") {
            MyReservationsScreen(navController)
        }
        composable("mypastreservations") {
            MyPastReservationsScreen(navController)
        }
        composable("ownerreservations") {
            OwnerReservationsScreen(navController)
        }
    }
}
