package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite

/**
 * Displays an editable form containing an existing campsite's details.
 *
 * @param campsite The campsite being edited.
 * @param onBack Callback invoked when the owner leaves the editing screen.
 */
@Composable
fun EditOwnerCampsiteScreen(
    campsite: Campsite,
    owner: CampsiteOwner,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var name by remember(campsite.id) {
        mutableStateOf(campsite.getName())
    }

    var location by remember(campsite.id) {
        mutableStateOf(campsite.getLocation())
    }

    var description by remember(campsite.id) {
        mutableStateOf(campsite.getDescription())
    }

    var capacity by remember(campsite.id) {
        mutableStateOf(campsite.getCapacity().toString())
    }

    var rules by remember(campsite.id) {
        mutableStateOf(campsite.getRules())
    }

    var status by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Text(
            "Edit Campsite",
            style = MaterialTheme.typography.headlineMedium
        )

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = capacity,
            onValueChange = { capacity = it },
            label = { Text("Capacity") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = rules,
            onValueChange = { rules = it },
            label = { Text("Rules") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val validCapacity = capacity.toIntOrNull()

                if (name.isBlank() || location.isBlank()) {
                    status = "Name and location are required."
                    return@Button
                }

                if (validCapacity == null || validCapacity <= 0) {
                    status = "Capacity must be a positive whole number."
                    return@Button
                }

                val updatedFields = mapOf<String, Any>(
                    "name" to name.trim(),
                    "location" to location.trim(),
                    "description" to description,
                    "capacity" to validCapacity,
                    "rules" to rules
                )

                isSaving = true
                status = "Saving..."

                owner.updateCampsite(campsite.id, updatedFields) { result ->
                    isSaving = false

                    if (result == "Updated") {
                        onSaved()
                    } else {
                        status = result
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Saving..." else "Save Changes")
        }

        if (status.isNotEmpty()) {
            Text(status)
        }
    }
}