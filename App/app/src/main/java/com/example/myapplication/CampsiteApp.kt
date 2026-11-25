package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atomiccryptids.campsiteapp.User

@Composable
fun CampsiteApp(session: AppSessionViewModel) {
    val state by session.accounts.state.collectAsState()
    val profile = state.profile

    // Display login until an account has been verified.
    if (profile == null) {
        LoginScreen(
            state = state,
            accounts = session.accounts
        )
        return
    }

    val navController = rememberNavController()

    val user: User = remember(profile) {
        when (profile.role) {
            "camper" -> Camper(
                uid = profile.uid,
                username = profile.username
            )

            "campsite owner" -> CampsiteOwner(
                uid = profile.uid,
                username = profile.username
            )

            "admin" -> Admin(
                uid = profile.uid,
                username = profile.username
            )

            else -> error("Unknown role: ${profile.role}")
        }
    }

    var campsite by session::campsite

    NavHost(
        navController = navController,
        startDestination = profile.route
    ) {
        composable("home") {
            ReturnToRoleHome(navController, profile.route)
        }

        composable("login") {
            LaunchedEffect(Unit) {
                // Clear the app session but preserve the Firebase UID.
                session.logout()
            }
        }

        composable("camper") {
            val camper = user as? Camper

            if (camper == null) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            CamperScreen(
                navController,
                camper,
                onSelectCampsite = { selected ->
                    campsite = selected
                }
            )
        }

        composable("owner") {
            val owner = user as? CampsiteOwner

            if (owner == null) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            OwnerScreen(
                navController = navController,
                owner = owner,
                onSelectCampsite = { selected ->
                    campsite = selected
                }
            )
        }

        composable("admin") {
            val admin = user as? Admin

            if (admin == null) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            AdminScreen(
                navController,
                admin,
                onSelectCampsite = { selected ->
                    campsite = selected
                }
            )
        }

        composable("owner-campsite-details") {
            val selected = campsite

            if (selected == null || user !is CampsiteOwner) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            OwnerCampsiteDetailsScreen(
                campsite = selected,
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
            val selected = campsite
            val owner = user as? CampsiteOwner

            if (selected == null || owner == null) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            OwnerWaitlistScreen(
                campsite = selected,
                owner = owner,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("edit-owner-campsite") {
            val selected = campsite
            val owner = user as? CampsiteOwner

            if (selected == null || owner == null) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            EditOwnerCampsiteScreen(
                campsite = selected,
                owner = owner,
                onBack = {
                    navController.popBackStack()
                },
                onSaved = {
                    val selectedId = selected.id

                    owner.getOwnedCampsites(
                        onResult = { updatedCampsites ->
                            val updated = updatedCampsites.firstOrNull {
                                it.id == selectedId
                            }

                            if (updated != null) {
                                campsite = updated
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

        composable("campsite") {
            val selected = campsite

            if (selected == null) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            CampsiteScreen(
                navController,
                selected,
                user
            )
        }

        composable("lottery") {
            val owner = user as? CampsiteOwner

            if (owner == null) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            LotteryScreen(
                ownerUid = owner.uid,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("wins") {
            if (user !is Camper) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            LotteryWinsScreen(
                navController,
                onSelectCampsite = { selected ->
                    campsite = selected
                }
            )
        }

        composable("reserve") {
            val selected = campsite

            if (selected == null || user !is Camper) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            ReserveScreen(
                navController,
                selected
            )
        }

        composable("myreservations") {
            if (user !is Camper) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            MyReservationsScreen(navController)
        }

        composable("mypastreservations") {
            if (user !is Camper) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            MyPastReservationsScreen(navController)
        }

        composable("ownerreservations") {
            if (user !is CampsiteOwner) {
                ReturnToRoleHome(navController, profile.route)
                return@composable
            }

            OwnerReservationsScreen(navController)
        }
    }
}

/**
 * Returns to the logged-in user's home screen when a destination
 * lacks its required campsite or has the wrong role.
 */
@Composable
private fun ReturnToRoleHome(
    navController: NavHostController,
    route: String
) {
    LaunchedEffect(navController, route) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) {
                inclusive = false
            }

            launchSingleTop = true
        }
    }
}