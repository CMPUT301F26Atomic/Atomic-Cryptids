package com.atomiccryptids.campsiteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    var user by remember { mutableStateOf<User?>(null) }

    BackHandler(enabled = user != null) { user = null }

    Column(modifier.padding(16.dp)) {
        when (val u = user) {
            null -> LoginScreen { user = it }
            is Camper -> CamperScreen(u) { user = null }
            is CampsiteOwner -> OwnerScreen(u) { user = null }
            else -> {
                Button(onClick = { user = null }) { Text("Back") }
                Text("Admin")
            }
        }
    }
}

@Composable
fun LoginScreen(onPick: (User) -> Unit) {
    Text("Pick your role")
    Button(onClick = { onPick(User().apply { makeAdmin(); saveUser() }) }) { Text("Admin") }
    Button(onClick = { onPick(CampsiteOwner().apply { makeCampsiteOwner(); saveUser() }) }) { Text("Campsite owner") }
    Button(onClick = { onPick(Camper().apply { makeCamper(); saveUser() }) }) { Text("Camper") }
}

@Composable
fun CamperScreen(camper: Camper, onBack: () -> Unit) {
    var campsites by remember { mutableStateOf<List<Campsite>?>(null) }
    var error by remember { mutableStateOf("") }

    Button(onClick = onBack) { Text("Back") }
    Button(onClick = {
        error = ""
        camper.searchCampsites({ campsites = it }, { error = it })
    }) { Text("Search campsite") }
    Text(error)

    campsites?.let { list ->
        if (list.isEmpty()) {
            Text("No campsites yet")
        }
        list.forEach { Text("${it.name} - ${it.location}") }
    }
}

@Composable
fun OwnerScreen(owner: CampsiteOwner, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Button(onClick = onBack) { Text("Back") }

    TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
    TextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
    TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
    TextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Capacity") })

    Button(onClick = {
        val campsite = owner.createCampsite(name, description, location, capacity.toIntOrNull() ?: 0)
        status = "Saving..."
        owner.postCampsite(campsite) { status = it }
        name = ""
        location = ""
        description = ""
        capacity = ""
    }) { Text("Add campsite") }

    Text(status)
}