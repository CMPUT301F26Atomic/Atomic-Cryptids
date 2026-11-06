package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Reservation
import com.google.firebase.auth.FirebaseAuth

//US 01.06.01 View list of active reservations
//US 01.07.01 Cancel reservation
//US 01.07.02 Confirmation of cancellation

@Composable
fun MyReservationsScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var refresh by remember { mutableIntStateOf(0) }
    var pendingCancel by remember { mutableStateOf<Reservation?>(null) }

    LaunchedEffect(refresh) {
        Reservation.fetchActiveForUser(
            uid,
            { reservations = it; loaded = true },
            { status = it },
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        Spacer(modifier = Modifier.height(16.dp))

        Text("My reservations", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (loaded && reservations.isEmpty()) {
            Text("No active reservations.")
        }

        reservations.forEach { reservation ->
            Text("${reservation.campsiteName} - ${reservation.campsiteLocation}")
            Row {
                Text(
                    "${formatUtcDate(reservation.startDateMillis)} - " +
                        formatUtcDate(reservation.endDateMillis)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = { pendingCancel = reservation }) { Text("Cancel") }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(status)
    }

    pendingCancel?.let { reservation ->
        AlertDialog(
            onDismissRequest = { pendingCancel = null },
            title = { Text("Cancel reservation?") },
            text = {
                Text(
                    "${reservation.campsiteName}, " +
                        "${formatUtcDate(reservation.startDateMillis)} - " +
                        formatUtcDate(reservation.endDateMillis)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    reservation.cancel { result ->
                        status = result
                        refresh++
                    }
                    pendingCancel = null
                }) { Text("Yes, cancel") }
            },
            dismissButton = {
                TextButton(onClick = { pendingCancel = null }) { Text("Keep it") }
            }
        )
    }
}
