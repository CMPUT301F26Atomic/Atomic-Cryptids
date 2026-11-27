package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect

@Composable

fun OwnerScreen(navController: NavController, owner: CampsiteOwner, onSelectCampsite: (Campsite) -> Unit) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var lastCampsite by remember { mutableStateOf<Campsite?>(null) }
    var qrMessage by remember { mutableStateOf("") }
    var availableStartDate by remember { mutableStateOf<Long?>(null) }
    var availableEndDate by remember { mutableStateOf<Long?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var ownedCampsites by remember { mutableStateOf<List<Campsite>>(emptyList()) }
    var isLoadingCampsites by remember { mutableStateOf(true) }
    var campsiteListError by remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        owner.getOwnedCampsites(
            onResult = { campsites ->
                ownedCampsites = campsites
                isLoadingCampsites = false
            },
            onError = { error ->
                campsiteListError = error
                isLoadingCampsites = false
            }
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
        TextButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        ) {
            Text("Back")
        }

        Text(
            text = "Owner dashboard",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Manage your campsites and reservations.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { navController.navigate("ownerreservations") },
                modifier = Modifier.weight(1f)
            ) {
                Text("View reservations")
            }

            //To Lottery Screen
            OutlinedButton(
                onClick = { navController.navigate("lottery") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Lotteries")
            }
        }

        Text(
            text = "My Campsites",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        when {
            isLoadingCampsites -> {
                Text("Loading campsites...")
            }

            campsiteListError.isNotEmpty() -> {
                Text(campsiteListError)
            }

            ownedCampsites.isEmpty() -> {
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
                            text = "You haven't created any campsites yet.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            else -> {
                ownedCampsites.forEach { campsite ->
                    Card(
                        onClick = {
                            onSelectCampsite(campsite)
                            navController.navigate("owner-campsite-details")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = campsite.getName(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = campsite.getLocation(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "›",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
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
                    text = "Create a campsite",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Campsite information",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacity") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rules,
                    onValueChange = { rules = it },
                    label = { Text("Rules") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                Text(
                    text = "Availability",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(
                        availableStartDate?.let {
                            java.text.SimpleDateFormat(
                                "MMM dd, yyyy",
                                java.util.Locale.getDefault()
                            ).apply {
                                timeZone = java.util.TimeZone.getTimeZone("UTC")
                            }.format(java.util.Date(it))
                        } ?: "Available from"
                    )
                }

                if (showStartDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = availableStartDate
                    )

                    DatePickerDialog(
                        onDismissRequest = { showStartDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    availableStartDate = datePickerState.selectedDateMillis
                                    showStartDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showStartDatePicker = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(
                        availableEndDate?.let {
                            java.text.SimpleDateFormat(
                                "MMM dd, yyyy",
                                java.util.Locale.getDefault()
                            ).apply {
                                timeZone = java.util.TimeZone.getTimeZone("UTC")
                            }.format(java.util.Date(it))
                        } ?: "Available through"
                    )
                }

                if (showEndDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = availableEndDate
                    )

                    DatePickerDialog(
                        onDismissRequest = { showEndDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    availableEndDate = datePickerState.selectedDateMillis
                                    showEndDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showEndDatePicker = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Button(
                    onClick = {
                        val start = availableStartDate
                        val end = availableEndDate

                        val validCapacity = capacity.toIntOrNull()

                        if (validCapacity == null || validCapacity <= 0) {
                            status = "Capacity must be a positive whole number."
                            return@Button
                        }

                        if (start == null || end == null) {
                            status = "Please select both availability dates."
                            return@Button
                        }

                        if (end < start) {
                            status = "End date cannot be before start date."
                            return@Button
                        }

                        val campsite = owner.createCampsite(
                            name,
                            validCapacity,
                            description,
                            location,
                            rules,
                            start,
                            end + 86_400_000L
                        )
                        status = "Saving..."

                        owner.postCampsite(campsite) { result ->
                            status = result

                            if (result == "Saved") {
                                lastCampsite = campsite
                                qrMessage = ""
                                ownedCampsites = ownedCampsites + campsite
                                name = ""
                                location = ""
                                description = ""
                                capacity = ""
                                rules = ""
                                availableStartDate = null
                                availableEndDate = null
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text("Add campsite")
                }

                if (status.isNotEmpty()) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                lastCampsite?.let { campsite ->
                    OutlinedButton(
                        onClick = { qrMessage = campsite.generateQRCode() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate QR code for ${campsite.getName()}")
                    }
                }
            }
        }
    }
}
