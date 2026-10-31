package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(onSelectOwner: () -> Unit, onSelectCamper: () -> Unit, navController: NavController) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {
            onSelectOwner()
            navController.navigate("home")
        }
        )
        {
            Text("Campsite owner")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onSelectCamper()
            navController.navigate("home")
        }
        ) {
            Text("Camper")
        }
    }

}