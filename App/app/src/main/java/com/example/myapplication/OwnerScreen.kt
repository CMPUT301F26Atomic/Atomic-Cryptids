package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@Composable
fun OwnerScreen(owner: CampsiteOwner, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var lastCampsite by remember { mutableStateOf<Campsite?>(null) }
    var qrMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
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

        //CommonButtons(owner, onBack)

        Column {
            TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Capacity") })
        }

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

        Spacer(modifier = Modifier.height(16.dp))

        Text(status)

        Spacer(modifier = Modifier.height(16.dp))

        lastCampsite?.let { campsite ->
            Button(onClick = { qrMessage = campsite.generateQRCode() }) {
                Text("Generate QR code for ${campsite.name}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(qrMessage)
    }

}