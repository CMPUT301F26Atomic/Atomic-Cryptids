package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton

//US 01.01.05 View campsite details
//US 01.01.06 Join lottery

@Composable
fun CamperScreen(navController: NavController, camper: Camper, onSelectCampsite: (Campsite) -> Unit) {
    var campsites by remember { mutableStateOf<List<Campsite>?>(null) }
    var message by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var locations by remember { mutableStateOf<List<String>>(emptyList()) }
    var guestCount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("campsites")
            .get()
            .addOnSuccessListener { snapshot ->
                locations = snapshot.documents
                    .mapNotNull { it.getString("location") }
                    .distinct()
                    .sorted()
            }
    }

    if (showSettings) {
        CamperProfileSetting(
            camper = camper,
            onBack = { showSettings = false },
            onDeleted = {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding().verticalScroll(rememberScrollState())) {
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showSettings = true }) { Text("Profile settings") }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            message = ""
            camper.searchCampsites({ campsites = it }, { message = it })
        }) { Text("Search campsite") }
        Text(message)

        Spacer(modifier = Modifier.height(16.dp))

        /*
        UI elements for choosing filters
        Filter options:
        Location
        Max occupancy
        Tags
        */
        Row(modifier = Modifier.fillMaxWidth()) {

            // Location
            //From OpenAI, ChatGPT when prompted with:
            //"How do I allow a user to choose various filters for a search function?"
            FilterMenu(
                menuData = locations,
                selectedLocation = selectedLocation,
                onLocationSelected = { location ->
                    selectedLocation = location
                }
            )


            OutlinedTextField(
                value = guestCount,
                onValueChange = { input ->
                    if (input.isEmpty() ||
                        (input.all { it.isDigit() } &&
                                (input.toIntOrNull() ?: 0) > 0)
                    ) {
                        guestCount = input
                    }
                },
                label = { Text("Number of guests") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
            Button(
                onClick = { showDatePicker = true }
            ) {
                Text(
                    selectedDate?.let {
                        java.text.SimpleDateFormat(
                            "MMM dd, yyyy",
                            java.util.Locale.getDefault()
                        ).apply {
                            timeZone = java.util.TimeZone.getTimeZone("UTC")
                        }.format(java.util.Date(it))
                    } ?: "Select date"
                )
            }

            if (selectedDate != null) {
                TextButton(
                    onClick = { selectedDate = null }
                ) {
                    Text("Clear date")
                }
            }
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedDate = datePickerState.selectedDateMillis
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }


                ) {
                    DatePicker(state = datePickerState)
                }
            }


        campsites?.let { list ->
            val filteredCampsites = filterCampsites(
                campsites = list,
                location = selectedLocation,
                guestCount = guestCount.toIntOrNull(),
                date = selectedDate
            )

            if (filteredCampsites.isEmpty()) {
                Text("No campsites match your filters")
            }

            filteredCampsites.forEach { campsite ->
                Button(onClick = {
                    onSelectCampsite(campsite)
                    navController.navigate("campsite")
                }) {
                    Text("${campsite.getName()} - ${campsite.getLocation()}")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}
