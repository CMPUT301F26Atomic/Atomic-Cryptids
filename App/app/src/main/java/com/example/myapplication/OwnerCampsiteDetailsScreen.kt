package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.Reservation

/**
 * Displays the details of a campsite belonging to the current owner.
 *
 * @param campsite The campsite whose details are displayed.
 * @param onBack Callback invoked when the owner returns to the previous screen.
 */
@Composable
fun OwnerCampsiteDetailsScreen(
    campsite: Campsite,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    var bookedReservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var bookedStatus by remember { mutableStateOf("") }

    LaunchedEffect(campsite.id) {
        Reservation.fetchActiveForCampsite(
            campsite.id,
            { bookedReservations = it },
            { bookedStatus = it },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Button(onClick = onEdit) {
            Text("Edit Campsite")
        }

        Text(
            text = campsite.getName(),
            style = MaterialTheme.typography.headlineMedium
        )

        HorizontalDivider()

        Text("Location: ${campsite.getLocation()}")
        Text("Description: ${campsite.getDescription()}")
        Text("Capacity: ${campsite.getCapacity()}")
        Text("Occupancy: ${campsite.getOccupancy()}")
        Text("Rules: ${campsite.getRules()}")

        HorizontalDivider()

        Text("Booked dates:", style = MaterialTheme.typography.titleMedium)
        if (bookedReservations.isEmpty()) {
            Text("No booked dates.")
        }
        bookedReservations.forEach {
            Text("${formatUtcDate(it.startDateMillis)} - ${formatUtcDate(it.endDateMillis)}")
        }
        Text(bookedStatus)
    }
}