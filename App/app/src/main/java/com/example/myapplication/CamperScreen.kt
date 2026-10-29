package com.example.myapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CamperScreen(
    camper: Camper,
    onBack: () -> Unit
) {
    var campsites by remember { mutableStateOf<List<Campsite>?>(null) }
    var message by remember { mutableStateOf("") }

    CommonButtons(camper, onBack)

    Button(onClick = {
        message = ""
        camper.searchCampsites({ campsites = it }, { message = it })
    }) { Text("Search campsite") }
    Text(message)

    campsites?.let { list ->
        if (list.isEmpty()) {
            Text("No campsites yet")
        }
        list.forEach { campsite ->
            Button(onClick = {
                camper.joinLottery(campsite)
                message = "Joined lottery: ${campsite.name}"
            }) { Text("${campsite.name} - ${campsite.location}") }
        }
    }

    /*
    * UI elements for choosing filters
    * Filter options:
    * Location
    * Max occupancy
    * Tags
    */
    Row(modifier = Modifier.fillMaxWidth()) {

        // Location
        // TODO get all location values from database

    }
}