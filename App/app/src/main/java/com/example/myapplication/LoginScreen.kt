package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController, onSelectCamper: () -> Unit, onSelectOwner: () -> Unit, onSelectAdmin: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Atomic Cryptids",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Campsite Management Application",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Select your role",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSelectCamper()
                navController.navigate("home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("camperLoginButton")
        ) {
            Text("Camper")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onSelectOwner()
                navController.navigate("home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("ownerLoginButton")
        ) {
            Text("Campsite owner")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onSelectAdmin()
                navController.navigate("home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("adminLoginButton")
        ) {
            Text("Admin")
        }

    }

}