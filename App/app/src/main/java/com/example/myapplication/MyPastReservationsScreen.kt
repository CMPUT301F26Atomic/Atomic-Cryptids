package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Reservation
import com.google.firebase.auth.FirebaseAuth

//US 01.06.02 View list of past reservations

/**
 * UI screen for lotteries
 *
 * @param ownerUid the id of the user that is currently logged in
 * @param onBack function triggered to navigate back to previous screen
 */

/**
 * Past Reservation screen
 * fetches reservations that ended before today and displays it
 */
@Composable
fun MyPastReservationsScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Reservation.fetchPastForUser(
            uid,
            { reservations = it; loaded = true },
            { status = it },
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Past reservations", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (loaded && reservations.isEmpty()) {
            Text("No past reservations.")
        }

        reservations.forEach { reservation ->
            Text("${reservation.campsiteName} - ${reservation.campsiteLocation}")
            Text(
                "${formatUtcDate(reservation.startDateMillis)} - " +
                        formatUtcDate(reservation.endDateMillis)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(status)
    }
}