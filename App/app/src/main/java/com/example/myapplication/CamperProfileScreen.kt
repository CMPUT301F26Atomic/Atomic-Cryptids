package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Displays a camper's personal information to an administrator.
 *
 * @param navController Controller used to navigate between screens.
 * @param camper The camper whose profile is being viewed.
 */
@Composable
fun CamperProfileScreen(
    navController: NavController,
    camper: Camper
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Back")
        }

        Text(
            text = "Camper profile",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "View registered camper information.",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Personal information",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(text = "Username: ${camper.username}")

                Text(
                    text = "Name",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = camper.getName().ifBlank { "Unnamed camper" },
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider()

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = camper.getEmail().ifBlank { "No email provided" },
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider()

                Text(
                    text = "Phone number",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = camper.getPhone().ifBlank { "No phone number provided" },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}