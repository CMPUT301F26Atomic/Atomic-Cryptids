package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton

@Composable

fun OwnerScreen(navController: NavController, owner: CampsiteOwner) {
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

        Column {
            TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Capacity") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = rules,
                onValueChange = { rules = it },
                label = { Text("Rules") }
            )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                    onClick = { showStartDatePicker = true }
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showEndDatePicker = true }
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
        }

        Button(onClick = {
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
                description,
                location,
                validCapacity,
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

                    name = ""
                    location = ""
                    description = ""
                    capacity = ""
                    rules = ""
                    availableStartDate = null
                    availableEndDate = null
                }
            }
        }) { Text("Add campsite") }

        Spacer(modifier = Modifier.height(16.dp))

        Text(status)

        Spacer(modifier = Modifier.height(16.dp))

        lastCampsite?.let { campsite ->
            Button(onClick = { qrMessage = campsite.generateQRCode() }) {
                Text("Generate QR code for ${campsite.getName()}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //To Lottery Screen
        Button(onClick = { navController.navigate("lottery") }) {
            Text("Lotteries")
        }
    }
}