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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//US 02.07.01 View list of active reservations as campsite owner
//US 02.07.02 View list of past reservations as campsite owner
//US 02.07.03 View list of cancelled reservations as campsite owner

@Composable
fun OwnerReservationsScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var items by remember { mutableStateOf<List<Pair<Campsite, List<Reservation>>>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Active") }

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
                    Reservation.fetchAllForCampsite(
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back")
        }

        Text(
            text = "Campsite reservations",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "View reservations across your campsites.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Active", "Past", "Cancelled").forEach { tab ->
                if (filter == tab) {
                    Button(onClick = { filter = tab }, modifier = Modifier.weight(1f)) {
                        Text(tab)
                    }
                } else {
                    OutlinedButton(onClick = { filter = tab }, modifier = Modifier.weight(1f)) {
                        Text(tab)
                    }
                }
            }
        }

        if (loaded && items.isEmpty()) {
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You have no campsites yet.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        val now = System.currentTimeMillis()
        items.forEach { (campsite, reservations) ->
            val shown = reservations.filter {
                when (filter) {
                    "Past" -> it.endDateMillis <= now && it.status != Reservation.Status.CANCELLED
                    "Cancelled" -> it.status == Reservation.Status.CANCELLED
                    else -> it.status == Reservation.Status.ACTIVE && it.endDateMillis > now
                }
            }
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = campsite.getName(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    if (shown.isEmpty()) {
                        Text(
                            text = "No ${filter.lowercase()} reservations.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    shown.forEach {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${formatUtcDate(it.startDateMillis)} - ${formatUtcDate(it.endDateMillis)}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = if (it.status == Reservation.Status.CANCELLED)
                                        "Cancelled by camper: ${it.userId.take(8)}"
                                    else "Camper: ${it.userId.take(8)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
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
}