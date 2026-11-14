package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@Composable
fun CamperProfileScreen(navController: NavController, camper: Camper) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (camper.getName() == "") {
            Text("Unnamed Camper")
        }
        else {
            Text(camper.getName())
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (camper.getEmail() == "") {
            Text("No email")
        }
        else {
            Text(camper.getEmail())
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (camper.getPhone() == "") {
            Text("No phone number")
        }
        else {
            Text(camper.getPhone())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back")
        }
    }
}