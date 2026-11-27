package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back")
            }

            // added this button that takes user to past reservations
            TextButton(onClick = { navController.navigate("mypastreservations") }) {
                Text("Past reservations")
            }
        }

        Text(
            text = "My reservations",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "View and manage your upcoming stays.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (loaded && reservations.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No active reservations.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        reservations.forEach { reservation ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = reservation.campsiteName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = reservation.campsiteLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Text(
                        text = "Reservation dates",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "${formatUtcDate(reservation.startDateMillis)} - " +
                                formatUtcDate(reservation.endDateMillis),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedButton(
                        onClick = { pendingCancel = reservation },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }

        if (status.isNotEmpty()) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
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