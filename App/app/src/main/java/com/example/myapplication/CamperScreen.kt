package com.example.myapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//US 01.01.05 View campsite details
//US 01.01.06 Join lottery

@Composable
fun CamperScreen(
    camper: Camper,
    onBack: () -> Unit
) {
    var campsites by remember { mutableStateOf<List<Campsite>?>(null) }
    var message by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    var selectedCampsite by remember { mutableStateOf<Campsite?>(null) }

    if (showSettings) {
        CamperProfileSetting(
            camper = camper,
            onBack = { showSettings = false },
            onDeleted = onBack
        )
        return
    }

    if (selectedCampsite != null) {
        showCampsiteDetails(camper = camper, campsite = selectedCampsite!!, onBack = onBack)
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = onBack) { Text("Back") }
        Button(onClick = { showSettings = true }) { Text("Profile settings") }

        Button(onClick = {
            camper.searchCampsites({ campsites = it }, { message = it })
        }) { Text("Search campsite") }

        Text(message)

        campsites?.let { list ->
            if (list.isEmpty()) {
                Text("no campsites")
            } else {
                list.forEach { campsite ->
                    Button(onClick = { selectedCampsite = campsite }) {
                        Text("${campsite.name} - ${campsite.location}")
                    }
                }
            }
        }
    }
}



    /*
    UI elements for choosing filters
    Filter options:
    Location
    Max occupancy
    Tags

    Row(modifier = Modifier.fillMaxWidth()) {

        // Location

        // Google Gemini: asked to get all values of a certain attribute in the collection without using coroutines
        var locations by remember { mutableStateOf<List<String>>(emptyList()) }

        FirebaseFirestore.getInstance().collection("campsites").get()
            .addOnSuccessListener { snapshot ->
                locations = snapshot.mapNotNull { it.getString("location") }
            }


        FilterMenu(locations, listOf())

        // TODO add filter menu for max occupancy
    }
}

     */