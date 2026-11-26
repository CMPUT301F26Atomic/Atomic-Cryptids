package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
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
import org.w3c.dom.Text

//US 02.07.01 View list of active reservations as campsite owner

@Composable
fun OwnerReservationsScreen(navController: NavController, owner: CampsiteOwner) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var items by remember { mutableStateOf<List<Pair<Campsite, List<Reservation>>>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var messageOpen by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

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
            text = "View active reservations across your campsites.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

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

        items.forEach { (campsite, reservations) ->
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

                    if (reservations.isEmpty()) {
                        Text(
                            text = "No active reservations.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    reservations.forEach { reservation ->
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
                                    text = "${formatUtcDate(reservation.startDateMillis)} - ${formatUtcDate(reservation.endDateMillis)}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "Camper: ${reservation.userId.take(8)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (!messageOpen) {
                                    Button(
                                        onClick = {
                                            messageOpen = true
                                        }
                                    ) {
                                        Text("Message camper")
                                    }
                                }
                                else {
                                    OutlinedTextField(
                                        value = message,
                                        onValueChange = { input ->
                                            message = input
                                        },
                                        label = { Text("Message") }
                                    )

                                    Button(
                                        onClick = {
                                            owner.sendMessage(reservation.userId, message)
                                            message = ""
                                            messageOpen = false
                                        }
                                    ) {
                                        Text("Send")
                                    }
                                }
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