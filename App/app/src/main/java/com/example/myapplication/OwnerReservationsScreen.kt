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
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//US 02.07.01 View list of active reservations as campsite owner

@Composable
fun OwnerReservationsScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var items by remember { mutableStateOf<List<Pair<Campsite, List<Reservation>>>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("campsites")
            .whereEqualTo("owner", uid)
            .get()
            .addOnSuccessListener { snap ->
                val campsites = snap.documents.mapNotNull { Campsite.fromDoc(it) }
                if (campsites.isEmpty()) {
                    items = emptyList()
                    loaded = true
                    return@addOnSuccessListener
                }
                val results = mutableListOf<Pair<Campsite, List<Reservation>>>()
                var remaining = campsites.size
                campsites.forEach { campsite ->
                    Reservation.fetchActiveForCampsite(
                        campsite.id,
                        { reservations ->
                            results.add(campsite to reservations)
                            remaining--
                            if (remaining == 0) {
                                items = results.sortedBy { it.first.getName() }
                                loaded = true
                            }
                        },
                        { error ->
                            status = error
                            remaining--
                            if (remaining == 0) {
                                items = results.sortedBy { it.first.getName() }
                                loaded = true
                            }
                        },
                    )
                }
            }
            .addOnFailureListener { status = "Error: ${it.message}" }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Reservations on my campsites", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (loaded && items.isEmpty()) {
            Text("You have no campsites yet.")
        }

        items.forEach { (campsite, reservations) ->
            Text(campsite.getName(), style = MaterialTheme.typography.titleMedium)
            if (reservations.isEmpty()) {
                Text("No active reservations.")
            }
            reservations.forEach {
                Text(
                    "${formatUtcDate(it.startDateMillis)} - ${formatUtcDate(it.endDateMillis)}" +
                        "  (camper: ${it.userId.take(8)})"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(status)
    }
}
