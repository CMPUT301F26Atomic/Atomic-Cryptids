package com.atomiccryptids.campsiteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.ui.theme.MyApplicationTheme
import com.example.myapplication.Camper
import com.example.myapplication.CampsiteOwner
import com.google.firebase.auth.FirebaseAuth

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

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
    }

    BackHandler(enabled = user != null) { user = null }

    Column(modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        when (val u = user) {
            null -> LoginScreen { user = it }
            is Camper -> CamperScreen(u) { user = null }
            is CampsiteOwner -> OwnerScreen(u) { user = null }
            else -> {}
        }
    }
}

@Composable
fun LoginScreen(onPick: (User) -> Unit) {
    Text("Pick your role")
    Button(onClick = { onPick(CampsiteOwner().apply { saveUser() }) }) { Text("Campsite owner") }
    Button(onClick = { onPick(Camper().apply { saveUser() }) }) { Text("Camper") }
}

@Composable
fun CommonButtons(user: User, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var notificationsOn by remember { mutableStateOf(false) }

    Button(onClick = onBack) { Text("Back") }
    Button(onClick = {
        notificationsOn = !notificationsOn
        message = user.optNotification(notificationsOn)
    }) { Text("Toggle notifications") }
    Button(onClick = {
        user.deleteProfile()
        onBack()
    }) { Text("Delete profile") }
    Text(message)
}

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

@Composable
fun OwnerScreen(owner: CampsiteOwner, onBack: () -> Unit) {
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
}