package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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

    LaunchedEffect(refresh) {
        Reservation.fetchActiveForCampsite(campsite.id, { existing = it }, { status = it })
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "${campsite.getName()} - ${campsite.getLocation()}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        val availStart = campsite.getAvailableStartDate()
        val availEnd = campsite.getAvailableEndDate()
        if (availStart != null && availEnd != null) {
            Text("Available: ${formatUtcDate(availStart)} - ${formatUtcDate(availEnd)}")
        } else {
            Text("No availability dates set for this campsite.")
        }

        if (existing.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Already booked:")
            existing.forEach {
                Text("${formatUtcDate(it.startDateMillis)} - ${formatUtcDate(it.endDateMillis)}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { showStartPicker = true }) {
                Text(startDate?.let { formatUtcDate(it) } ?: "Check-in date")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { showEndPicker = true }) {
                Text(endDate?.let { formatUtcDate(it) } ?: "Check-out date")
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
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
                Reservation(
                    campsiteId = campsite.id,
                    campsiteName = campsite.getName(),
                    campsiteLocation = campsite.getLocation(),
                    userId = uid,
                    startDateMillis = startDate!!,
                    endDateMillis = endDate!!,
                ).save { result ->
                    status = if (result == "Saved") "Reserved!" else result
                    if (result == "Saved") {
                        startDate = null
                        endDate = null
                        refresh++
                    }
                }
            }
        }) { Text("Reserve") }

        Spacer(modifier = Modifier.height(8.dp))

        Text(status)
    }
}
