package com.example.myapplication

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.atomiccryptids.campsiteapp.Campsite

@Composable
fun OwnerScreen(owner: CampsiteOwner, onBack: () -> Unit = {}, onNavigateToLottery: ()-> Unit = {}) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var lastCampsite by remember { mutableStateOf<Campsite?>(null) }
    var qrMessage by remember { mutableStateOf("") }

    CommonButtons(owner, onBack)

    TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
    TextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
    TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
    TextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Capacity") })

    Button(onClick = {
        val campsite = owner.createCampsite(name, description, location, capacity.toIntOrNull() ?: 0)
        status = "Saving..."
        owner.postCampsite(campsite) { status = it }
        lastCampsite = campsite
        qrMessage = ""
        name = ""
        location = ""
        description = ""
        capacity = ""
    }) { Text("Add campsite") }

    Text(status)

    lastCampsite?.let { campsite ->
        Button(onClick = { qrMessage = campsite.generateQRCode() }) {
            Text("Generate QR code for ${campsite.name}")
        }
    }
    Text(qrMessage)

    //To Lottery Screen
    Button(onClick = { onNavigateToLottery() }) {
        Text("Lotteries")
    }
}