/**
 * UI screen for creating/editing lotteries
 */
package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LotteryScreen(ownerUid: String, onBack: () -> Unit = {}) {
    var allCampsites by remember { mutableStateOf<List<Campsite>>(emptyList()) }
    var selectedCampsites by remember { mutableStateOf<List<Campsite>>(emptyList()) }
    var existingLotteryId by remember { mutableStateOf<String?>(null) }
    var winnersInput by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    // fetch campsite
    LaunchedEffect(ownerUid) {
        db.collection("campsites")
            .whereEqualTo("owner", "owner")
            .get()
            .addOnSuccessListener { snapshot ->
                allCampsites = snapshot.documents.mapNotNull { doc ->
                    val rawName = doc.getString("name") ?: ""
                    val cleanName = rawName.trim()
                    if (cleanName.isBlank()) return@mapNotNull null
                    val cap = doc.getLong("capacity")?.toInt() ?: 0
                    val occ = doc.getLong("occupancy")?.toInt() ?: 0

                    Campsite(
                        id = doc.id,
                        capacity = cap,
                        name = cleanName,
                        owner = doc.getString("owner") ?: "",
                        description = doc.getString("description") ?: "",
                        location = doc.getString("location") ?: "",
                        occupancy = occ
                    )
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Create / Edit Lottery", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // dropdown menu
        Box {
            Button(onClick = { expanded = true }) {
                Text("Add Campsite to Lottery")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                allCampsites.forEach { campsite ->
                    DropdownMenuItem(
                        text = { Text(campsite.name) },
                        onClick = {
                            expanded = false
                            if (!selectedCampsites.any { it.id == campsite.id }) {
                                selectedCampsites = selectedCampsites + campsite

                                // check if editing
                                db.collection("lotteries")
                                    .whereArrayContains("campsiteIds", campsite.id)
                                    .get()
                                    .addOnSuccessListener { snap ->
                                        if (!snap.isEmpty) {
                                            val lot = snap.documents[0]
                                            existingLotteryId = lot.id
                                            winnersInput = lot.getLong("numberOfWinners")?.toString() ?: ""
                                            status = "Editing existing lottery"
                                        }
                                    }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Campsites:", style = MaterialTheme.typography.titleMedium)
        selectedCampsites.forEach { Text("- ${it.name}") }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = winnersInput,
            onValueChange = {
                winnersInput = it
                status = ""
            },
            label = { Text("Number of winners") }, modifier = Modifier.fillMaxWidth())

        Text(text = status, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                if (!LotteryValidator.isValidWinnerCount(winnersInput)) {
                    status = "Error: Winner count must be a positive whole number."
                    return@Button
                }

                val winnersInt = winnersInput.toInt()
                val campsiteIds = selectedCampsites.map { it.id }
                if (campsiteIds.isEmpty()) {
                    status = "Error: Select at least one campsite."
                    return@Button
                }

                val lotteryData = mapOf("campsiteIds" to campsiteIds, "numberOfWinners" to winnersInt, "ownerId" to ownerUid, "waitlist" to emptyList<String>())

                if (existingLotteryId != null) {
                    db.collection("lotteries").document(existingLotteryId!!).update(lotteryData).addOnSuccessListener { status = "Lottery updated!" }
                } else {
                    db.collection("lotteries").add(lotteryData).addOnSuccessListener { docRef ->
                            existingLotteryId = docRef.id
                            status = "New lottery created!"
                        }
                }
            }) {
                Text("Save")
            }
            Button(onClick = onBack) { Text("Back") }
        }
    }
}