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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.firestore.FirebaseFirestore

//US 01.01.05 View campsite details
//US 01.01.06 Join lottery

@Composable
fun CamperScreen(navController: NavController, camper: Camper, onSelectCampsite: (Campsite) -> Unit) {
    var campsites by remember { mutableStateOf<List<Campsite>?>(null) }
    var message by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }

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

            // Google Gemini: asked to get all values of a certain attribute in the collection without using coroutines
            var locations by remember { mutableStateOf<List<String>>(emptyList()) }

            FirebaseFirestore.getInstance().collection("campsites").get()
                .addOnSuccessListener { snapshot ->
                    locations = snapshot.mapNotNull { it.getString("location") }
                }

            FilterMenu(locations, listOf())

            // TODO add filter menu for max occupancy
        }

        campsites?.let { list ->
            if (list.isEmpty()) {
                Text("No campsites yet")
            }
            list.forEach { campsite ->
                Button(onClick = {
                    onSelectCampsite(campsite)
                    navController.navigate("campsite")

                }) { Text("${campsite.getName()} - ${campsite.getLocation()}") }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}
