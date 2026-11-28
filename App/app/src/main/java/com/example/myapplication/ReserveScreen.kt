package com.example.myapplication

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.Reservation
import com.google.firebase.auth.FirebaseAuth

//US 01.04.01 View available reservation dates
//US 01.04.02 Choose which campsite to reserve

@Composable
fun ReserveScreen(navController: NavController, campsite: Campsite) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var existing by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var refresh by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    var confirmedReservation by remember {
        mutableStateOf<Reservation?>(null)
    }

    var pendingCsv by remember {
        mutableStateOf<String?>(null)
    }

    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val csv = pendingCsv

        if (uri != null && csv != null) {
            ReservationCsvDownload.save(context, uri, csv) {
                status = it
            }
        }

        pendingCsv = null
    }

    LaunchedEffect(refresh) {
        Reservation.fetchActiveForCampsite(campsite.id, { existing = it }, { status = it })
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
            text = "Reserve campsite",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Choose the dates for your stay.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = campsite.getName(),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = campsite.getLocation(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        val availStart = campsite.getAvailableStartDate()
        val availEnd = campsite.getAvailableEndDate()

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
                    text = "Availability",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                if (availStart != null && availEnd != null) {
                    Text(
                        text = "Available dates",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${formatUtcDate(availStart)} - ${formatUtcDate(availEnd)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text("No availability dates set for this campsite.")
                }

                if (existing.isNotEmpty()) {
                    HorizontalDivider()

                    Text(
                        text = "Already booked:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    existing.forEach {
                        Text(
                            text = "${formatUtcDate(it.startDateMillis)} - ${formatUtcDate(it.endDateMillis)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                    text = "Your stay",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Check-in",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(
                    onClick = { showStartPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(startDate?.let { formatUtcDate(it) } ?: "Check-in date")
                }

                Text(
                    text = "Check-out",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(
                    onClick = { showEndPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(endDate?.let { formatUtcDate(it) } ?: "Check-out date")
                }
            }
        }

        if (showStartPicker) {
            val pickerState = rememberDatePickerState(initialSelectedDateMillis = startDate)
            DatePickerDialog(
                onDismissRequest = { showStartPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDate = pickerState.selectedDateMillis
                        showStartPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
                }
            ) { DatePicker(state = pickerState) }
        }

        if (showEndPicker) {
            val pickerState = rememberDatePickerState(initialSelectedDateMillis = endDate)
            DatePickerDialog(
                onDismissRequest = { showEndPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDate = pickerState.selectedDateMillis
                        showEndPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
                }
            ) { DatePicker(state = pickerState) }
        }

        Button(
            onClick = {
                val error = ReservationValidator.validateRange(
                    startDateMillis = startDate,
                    endDateMillis = endDate,
                    availableStartDate = availStart,
                    availableEndDate = availEnd,
                    existingReservations = existing,
                )
                if (error != null) {
                    status = error
                } else {
                    val reservation = Reservation(
                        campsiteId = campsite.id,
                        campsiteName = campsite.getName(),
                        campsiteLocation = campsite.getLocation(),
                        userId = uid,
                        startDateMillis = startDate!!,
                        endDateMillis = endDate!!,
                    )

                    reservation.save { result ->
                        status = if (result == "Saved") "Reserved!" else result

                        if (result == "Saved") {
                            confirmedReservation = reservation
                            startDate = null
                            endDate = null
                            refresh++
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text("Reserve")
        }

        if (status.isNotEmpty()) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    confirmedReservation?.let { reservation ->
        ReservationConfirmationDialog(
            reservation = reservation,
            isCancellation = false,
            onDismiss = {
                confirmedReservation = null
            },
            onDownloadCsv = {
                pendingCsv = ReservationCsvExporter.generate(
                    reservation,
                    isCancellation = false
                )

                csvLauncher.launch("reservation-confirmation.csv")
            }
        )
    }
}