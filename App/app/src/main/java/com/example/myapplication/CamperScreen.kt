package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun CamperScreen(camper: Camper, navController: NavController) {
    var campsites by remember { mutableStateOf<List<Campsite>?>(null) }
    var message by remember { mutableStateOf("") }

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

        // CommonButtons(camper, onBack)

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

    campsites?.let { list ->
        if (list.isEmpty()) {
            Text("No campsites yet")
        }
        list.forEach { campsite ->
            Button(onClick = {
                camper.joinLottery(campsite)
                message = "Joined lottery: ${campsite.name}"
            }) { Text("${campsite.name} - ${campsite.location}") }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}