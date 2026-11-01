/**
 * UI screen for lotteries
 *
 * @param ownerUid the id of the user that is currently logged in
 * @param onBack function triggered to navigate back to previous screen
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
    var allCampsites by remember { mutableStateOf<List<Campsite>>(emptyList()) } //list of campsites for dropdown
    var selectedCampsites by remember { mutableStateOf<List<Campsite>>(emptyList()) } //specific campsite used in current lottery
    var existingLotteryId by remember { mutableStateOf<String?>(null) } //if null make new lottery, if not edit existing
    var winnersInput by remember { mutableStateOf("") } //num of winners
    var status by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    // fetch campsite
    LaunchedEffect(ownerUid) {
        db.collection("campsites")
            .whereEqualTo("owner", "owner").get() //only use campsites belonging to user
            .addOnSuccessListener { snapshot ->
                allCampsites = snapshot.documents.mapNotNull { doc -> //loop through all elements returned by firebase, skip all null values
                    val rawName = doc.getString("name") ?: ""
                    val cleanName = rawName.trim() //remove whitespace and other bullshit
                    if (cleanName.isBlank()) return@mapNotNull null //reject campsite without name
                    val cap = doc.getLong("capacity")?.toInt() ?: 0
                    val occ = doc.getLong("occupancy")?.toInt() ?: 0

                    //map firebase data into campsite
                    Campsite(id = doc.id, capacity = cap, name = cleanName, owner = doc.getString("owner") ?: "", description = doc.getString("description") ?: "", location = doc.getString("location") ?: "", occupancy = occ)
                }
            }
    }

    //UI
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Create / Edit Lottery", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // dropdown menu
        Box {
            Button(onClick = { expanded = true }) {
                Text("Add Campsite to Lottery")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { //close dropdown if user clicks outside of menu
                allCampsites.forEach { campsite -> //clickable row for each item
                    DropdownMenuItem(
                        text = { Text(campsite.name) },
                        onClick = {
                            expanded = false //close menu when item is selected
                            if (!selectedCampsites.any { it.id == campsite.id }) { //check if clicked campsite has already been selected
                                selectedCampsites = selectedCampsites + campsite

                                // check if editing
                                db.collection("lotteries")
                                    .whereArrayContains("campsiteIds", campsite.id).get() //check lotteries collection to see if chosen campsite is already in a lottery
                                    .addOnSuccessListener { snap ->
                                        if (!snap.isEmpty) { //if campsite is already in lottery
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
        selectedCampsites.forEach { Text("- ${it.name}") } //display chosen campsite name

        Spacer(modifier = Modifier.height(16.dp))

        //number of winners text box
        TextField(value = winnersInput, onValueChange = {
                winnersInput = it
                status = ""
            },
            label = { Text("Number of winners") }, modifier = Modifier.fillMaxWidth())

        Text(text = status, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { //buttons side by side
            Button(onClick = {
                if (!LotteryValidator.isValidWinnerCount(winnersInput)) { //check input
                    status = "Error: Winner count must be a positive whole number."
                    return@Button
                }

                val winnersInt = winnersInput.toInt()
                val campsiteIds = selectedCampsites.map { it.id } //pull id from the Campsite
                if (campsiteIds.isEmpty()) {
                    status = "Error: Select at least one campsite."
                    return@Button
                }

                val lotteryData = mapOf("campsiteIds" to campsiteIds, "numberOfWinners" to winnersInt, "ownerId" to ownerUid, "waitlist" to emptyList<String>()) //package data for firebase

                if (existingLotteryId != null) { //if editing existing lottery update to overwrite its data
                    db.collection("lotteries").document(existingLotteryId!!).update(lotteryData).addOnSuccessListener { status = "Lottery updated!" }
                } else { //if not editing then create new lottery
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