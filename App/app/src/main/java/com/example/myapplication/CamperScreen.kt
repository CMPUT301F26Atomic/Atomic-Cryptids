package com.example.myapplication

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.atomiccryptids.campsiteapp.Campsite

@Composable
fun CamperScreen(camper: Camper, onBack: () -> Unit) {
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
}