package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite
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
    var status by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        Text("Create / Edit Lottery", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier =
            Modifier.height(16.dp))

        // dropdown menu
        Box {
            Button(onClick = { expanded = true }) {
                Text("Add Campsite to Lottery")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 280.dp)) {
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
                                            startDateMillis = lot.getLong("startDate")
                                            endDateMillis = lot.getLong("endDate")
                                            regStartDateMillis = lot.getLong("registrationStartDate")
                                            regEndDateMillis = lot.getLong("registrationEndDate")
                                            capacityInput = lot.getLong("capacity")?.toString() ?: ""
                            
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


        Spacer(modifier = Modifier.height(16.dp))
        Text("Selected Campsites:", style = MaterialTheme.typography.titleMedium)
        selectedCampsites.forEach { campsite ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("- ${campsite.getName()}")

                Spacer(modifier = Modifier.width(8.dp))

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
                                    startDateMillis = null
                                    endDateMillis = null
                                    regStartDateMillis = null
                                    regEndDateMillis = null
                                    status = "Lottery deleted because no campsites remain."
                                }
                                .addOnFailureListener { e ->
                                    status = "Error deleting lottery: ${e.message}"
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("-")
                }
            }
        }

        TextField(value = capacityInput, onValueChange = {
            capacityInput = it
            status = ""
        },
            label = { Text("Lottery capacity") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showStartDatePicker = true }, modifier = Modifier.weight(1f)) {
                Text(
                    startDateMillis?.let { formatUtcDate(it) } ?: "Start Date"
                )
            }
            Button(onClick = { showEndDatePicker = true }, modifier = Modifier.weight(1f)) {
                Text(
                    endDateMillis?.let { formatUtcDate(it) } ?: "End Date"
                )
            }
        }

        if (showStartDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDateMillis = datePickerState.selectedDateMillis
                        showStartDatePicker = false
                        isConfirming = false
                        status = ""
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showEndDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDateMillis)
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDateMillis = datePickerState.selectedDateMillis
                        showEndDatePicker = false
                        isConfirming = false
                        status = ""
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showRegPicker = true }, modifier = Modifier.fillMaxWidth()) {
            Text(
                if (regStartDateMillis != null && regEndDateMillis != null)
                    "Registration: ${formatUtcDate(regStartDateMillis!!)} - ${formatUtcDate(regEndDateMillis!!)}"
                else "Set Registration Period"
            )
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

        //number of winners text box
        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = winnersInput, onValueChange = {
            winnersInput = it
            isConfirming = false
            status = ""
        },
            label = { Text("Number of winners") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = status, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
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
                if (startDateMillis == null || endDateMillis == null) {
                    status = "Error: Please specify both start and end dates for the lottery period."
                    isConfirming = false
                    return@Button
                }

                if (endDateMillis!! <= startDateMillis!!) {
                    status = "Error: End date must be after start date."
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
                            startDateMillis!! <= existingEnd && endDateMillis!! >= existingStart
                        }

                        if (hasOverlap) {
                            status = "Error: A campsite in your selection already has an active lottery during this period."
                            isConfirming = false
                        } else {
                            if (!isConfirming) {
                                isConfirming = true
                                status = "Review the details above. Click 'Confirm Save' to finalize."
                            } else {
                                val lotteryData = mapOf(
                                    "campsiteIds" to campsiteIds,
                                    "numberOfWinners" to winnersInt,
                                    "capacity" to capacityInt,
                                    "ownerId" to ownerUid,
                                    "startDate" to startDateMillis!!,
                                    "endDate" to endDateMillis!!,
                                    "registrationStartDate" to regStartDateMillis!!,
                                    "registrationEndDate" to regEndDateMillis!!
                                )

                                if (existingLotteryId != null) {
                                    val lotteryRef = db.collection("lotteries").document(existingLotteryId!!)

                                    db.runTransaction { transaction ->
                                        val snapshot = transaction.get(lotteryRef)

                                        if (snapshot.getBoolean("drawCompleted") == true) {
                                            throw IllegalStateException("This lottery has already been drawn.")
                                        }

                                        if (snapshot.getString("ownerId") != ownerUid) {
                                            throw IllegalStateException("You do not own this lottery.")
                                        }

                                        transaction.update(lotteryRef, lotteryData)
                                        null
                                    }
                                        .addOnSuccessListener {
                                            existingLotteryId = null
                                            selectedCampsites = emptyList()
                                            winnersInput = ""
                                            startDateMillis = null
                                            endDateMillis = null
                                            regStartDateMillis = null
                                            regEndDateMillis = null
                                            isConfirming = false
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
                                            startDateMillis = null
                                            endDateMillis = null
                                            regStartDateMillis = null
                                            regEndDateMillis = null
                                            isConfirming = false
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
            }) {
                Text(if (isConfirming) "Confirm Save" else "Save Listing")
            }
            Button(onClick = onBack) { Text("Back") }
        }
    }
}