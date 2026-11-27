package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment



/**
 * UI screen for lotteries
 *
 * @param ownerUid the id of the user that is currently logged in
 * @param onBack function triggered to navigate back to previous screen
 */
@Composable
fun LotteryScreen(ownerUid: String, onBack: () -> Unit = {}) {
    var allCampsites by remember { mutableStateOf<List<Campsite>>(emptyList()) } //list of campsites for dropdown
    var selectedCampsites by remember { mutableStateOf<List<Campsite>>(emptyList()) } //specific campsite used in current lottery
    var existingLotteryId by remember { mutableStateOf<String?>(null) } //if null make new lottery, if not edit existing
    var winnersInput by remember { mutableStateOf("") } //num of winners
    var capacityInput by remember { mutableStateOf("") } //max people in the lottery
    var waitlistCapacityInput by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var regStartDateMillis by remember { mutableStateOf<Long?>(null) } //registration window open
    var regEndDateMillis by remember { mutableStateOf<Long?>(null) } //registration window close
    var showRegPicker by remember { mutableStateOf(false) }
    var isConfirming by remember { mutableStateOf(false) }
    var drawCompleted by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    // fetch campsite
    LaunchedEffect(ownerUid) {
        db.collection("campsites")
            .whereEqualTo("owner", ownerUid).get() //only use campsites belonging to user
            .addOnSuccessListener { snapshot ->
                allCampsites = snapshot.documents.mapNotNull { doc -> //loop through all elements returned by firebase, skip all null values
                    val rawName = doc.getString("name") ?: ""
                    val cleanName = rawName.trim() //remove whitespace and other bullshit
                    if (cleanName.isBlank()) return@mapNotNull null //reject campsite without name
                    val cap = doc.getLong("capacity")?.toInt() ?: 0
                    val occ = doc.getLong("occupancy")?.toInt() ?: 0
                    Campsite(id = doc.id, capacity = cap, name = cleanName, owner = doc.getString("owner") ?: "", description = doc.getString("description") ?: "", location = doc.getString("location") ?: "", occupancy = occ)
                }
            }
    }

    //UI
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
        TextButton(onClick = onBack) {
            Text("Back")
        }

        Text(
            text = "Create / Edit Lottery",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Configure and manage your campsite lotteries.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

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
                    text = "Campsites",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // dropdown menu
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Add Campsite to Lottery")
                            Text("▾")
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.heightIn(max = 280.dp),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        allCampsites.forEach { campsite ->
                            DropdownMenuItem(
                                text = { Text(campsite.getName()) },
                                onClick = {
                                    expanded = false
                                    isConfirming = false
                                    status = ""
                                    if (!selectedCampsites.any { it.id == campsite.id }) {
                                        selectedCampsites = selectedCampsites + campsite

                                        // check if editing
                                        db.collection("lotteries")
                                            .whereArrayContains("campsiteIds", campsite.id).get()
                                            .addOnSuccessListener { snap ->
                                                if (!snap.isEmpty) {
                                                    val lot = snap.documents[0]
                                                    existingLotteryId = lot.id
                                                    drawCompleted = lot.getBoolean("drawCompleted") ?: false
                                                    winnersInput = lot.getLong("numberOfWinners")?.toString() ?: ""
                                                    regStartDateMillis = lot.getLong("registrationStartDate")
                                                    regEndDateMillis = lot.getLong("registrationEndDate")
                                                    capacityInput = lot.getLong("capacity")?.toString() ?: ""
                                                    waitlistCapacityInput = lot.getLong("waitlistCapacity")?.toString() ?: ""

                                                    status = "Editing existing lottery"
                                                    val lotteryCampsiteIds = lot.get("campsiteIds") as? List<String> ?: emptyList()
                                                    val associatedCampsites = allCampsites.filter { it.id in lotteryCampsiteIds }
                                                    selectedCampsites = (selectedCampsites + associatedCampsites).distinctBy { it.id }
                                                }
                                            }
                                    }
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Selected Campsites:",
                    style = MaterialTheme.typography.titleMedium
                )

                selectedCampsites.forEach { campsite ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = campsite.getName(),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Button(
                                onClick = {
                                    if (drawCompleted && existingLotteryId != null) {
                                        status = "This lottery has already been drawn and cannot be modified."
                                        return@Button
                                    }
                                    isConfirming = false
                                    selectedCampsites = selectedCampsites.filter { it.id != campsite.id }
                                    if (selectedCampsites.isEmpty() && existingLotteryId != null) {
                                        db.collection("lotteries").document(existingLotteryId!!)
                                            .delete()
                                            .addOnSuccessListener {
                                                existingLotteryId = null
                                                winnersInput = ""
                                                waitlistCapacityInput = ""
                                                regStartDateMillis = null
                                                regEndDateMillis = null
                                                capacityInput = ""
                                                waitlistCapacityInput = ""
                                                status = "Lottery deleted because no campsites remain."
                                            }
                                            .addOnFailureListener { e ->
                                                status = "Error deleting lottery: ${e.message}"
                                            }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            ) {
                                Text("-")
                            }
                        }
                    }
                }
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
                    text = "Lottery configuration",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = capacityInput,
                    onValueChange = {
                        capacityInput = it
                        isConfirming = false
                        status = ""
                    },
                    label = { Text("Lottery capacity") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = waitlistCapacityInput,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() ?: 0) > 0) {
                            waitlistCapacityInput = it
                            isConfirming = false
                            status = ""
                        }
                    },
                    label = { Text("Waitlist capacity (optional, empty = unlimited)") },
                    modifier = Modifier.fillMaxWidth()
                )

                //number of winners text box
                OutlinedTextField(
                    value = winnersInput,
                    onValueChange = {
                        winnersInput = it
                        isConfirming = false
                        status = ""
                    },
                    label = { Text("Number of winners") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                    text = "Registration period",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Choose when campers can enter the lottery.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = { showRegPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(
                        if (regStartDateMillis != null && regEndDateMillis != null)
                            "Registration: ${formatUtcDate(regStartDateMillis!!)} - ${formatUtcDate(regEndDateMillis!!)}"
                        else "Set Registration Period"
                    )
                }
            }
        }

        if (showRegPicker) {
            val rangeState = rememberDateRangePickerState(
                initialSelectedStartDateMillis = regStartDateMillis,
                initialSelectedEndDateMillis = regEndDateMillis
            )
            DatePickerDialog(
                onDismissRequest = { showRegPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        regStartDateMillis = rangeState.selectedStartDateMillis
                        regEndDateMillis = rangeState.selectedEndDateMillis
                        showRegPicker = false
                        isConfirming = false
                        status = ""
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showRegPicker = false }) { Text("Cancel") }
                }
            ) {
                DateRangePicker(state = rangeState, modifier = Modifier.weight(1f))
            }
        }

        if (status.isNotEmpty()) {
            Text(
                text = status,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                if (drawCompleted && existingLotteryId != null) {
                    status = "This lottery has already been drawn and cannot be edited."
                    isConfirming = false
                    return@Button
                }
                if (!LotteryValidator.isValidWinnerCount(winnersInput)) {
                    status = "Error: Winner count must be a positive whole number."
                    isConfirming = false
                    return@Button
                }
                val winnersInt = winnersInput.toInt()
                val campsiteIds = selectedCampsites.map { it.id }

                if (campsiteIds.isEmpty()) {
                    status = "Error: Select at least one campsite."
                    isConfirming = false
                    return@Button
                }
                if (!LotteryValidator.isValidPeriod(regStartDateMillis, regEndDateMillis)) {
                    status = "Error: Registration close date must be after the open date."
                    isConfirming = false
                    return@Button
                }

                if (!LotteryValidator.isValidWinnerCount(capacityInput)) {
                    status = "Error: Capacity count must be a positive whole number."
                    return@Button
                }


                val capacityInt = capacityInput.toInt()


                if (campsiteIds.isEmpty()) {
                    status = "Error: Select at least one campsite."
                    return@Button
                }
                db.collection("lotteries")
                    .whereArrayContainsAny("campsiteIds", campsiteIds)
                    .get()
                    .addOnSuccessListener { snap ->
                        val hasOverlap = snap.documents.any { doc ->
                            if (existingLotteryId != null && doc.id == existingLotteryId) return@any false
                            val existingStart = doc.getLong("startDate") ?: 0L
                            val existingEnd = doc.getLong("endDate") ?: 0L
                            regStartDateMillis!! <= existingEnd && regEndDateMillis!! >= existingStart
                        }

                        if (hasOverlap) {
                            status = "Error: A campsite in your selection already has an active lottery during this period."
                            isConfirming = false
                        } else {
                            if (!isConfirming) {
                                isConfirming = true
                                status = "Review the details above. Click 'Confirm Save' to finalize."
                            } else {
                                val waitlistCapacityLong = waitlistCapacityInput.toIntOrNull()?.toLong()
                                val lotteryData = buildMap<String, Any> {
                                    put("campsiteIds", campsiteIds)
                                    put("numberOfWinners", winnersInt)
                                    put("capacity", capacityInt)
                                    put("ownerId", ownerUid)
                                    put("startDate", regStartDateMillis!!)
                                    put("endDate", regEndDateMillis!!)
                                    put("registrationStartDate", regStartDateMillis!!)
                                    put("registrationEndDate", regEndDateMillis!!)
                                    if (waitlistCapacityLong != null) {
                                        put("waitlistCapacity", waitlistCapacityLong)
                                    }
                                }

                                if (existingLotteryId != null) {
                                    val lotteryRef = db.collection("lotteries").document(existingLotteryId!!)
                                    val updateData = lotteryData.toMutableMap()
                                    if (waitlistCapacityLong == null) {
                                        updateData["waitlistCapacity"] = FieldValue.delete()
                                    }

                                    db.runTransaction { transaction ->
                                        val snapshot = transaction.get(lotteryRef)

                                        if (snapshot.getBoolean("drawCompleted") == true) {
                                            throw IllegalStateException("This lottery has already been drawn.")
                                        }

                                        if (snapshot.getString("ownerId") != ownerUid) {
                                            throw IllegalStateException("You do not own this lottery.")
                                        }

                                        transaction.update(lotteryRef, updateData)
                                        null
                                    }
                                        .addOnSuccessListener {
                                            existingLotteryId = null
                                            selectedCampsites = emptyList()
                                            winnersInput = ""
                                            waitlistCapacityInput = ""
                                            regStartDateMillis = null
                                            regEndDateMillis = null
                                            isConfirming = false
                                            capacityInput = ""
                                            status = "Lottery updated successfully!"
                                        }
                                        .addOnFailureListener { e ->
                                            status = "Error updating lottery: ${e.message}"
                                            isConfirming = false
                                        }
                                } else {
                                    val newLotteryData = lotteryData + mapOf(
                                        "entrants" to emptyList<String>(),
                                        "waitlist" to emptyList<String>(),
                                        "drawCompleted" to false
                                    )
                                    db.collection("lotteries").add(newLotteryData)
                                        .addOnSuccessListener {
                                            existingLotteryId = null
                                            selectedCampsites = emptyList()
                                            winnersInput = ""
                                            waitlistCapacityInput = ""
                                            regStartDateMillis = null
                                            regEndDateMillis = null
                                            isConfirming = false
                                            capacityInput = ""
                                            status = "New lottery listing created!"
                                        }
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        status = "Database error: ${e.message}"
                        isConfirming = false
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(if (isConfirming) "Confirm Save" else "Save Listing")
        }
    }
}