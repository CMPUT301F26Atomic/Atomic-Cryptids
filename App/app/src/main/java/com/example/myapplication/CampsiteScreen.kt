package com.example.myapplication

import android.widget.Button
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
import com.atomiccryptids.campsiteapp.Campsite

@Composable
fun CampsiteScreen(navController: NavController, campsite: Campsite) {
    val campsiteDetails = campsite.getCampsite()

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Capacity: ${campsiteDetails["capacity"].toString()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Name: ${campsiteDetails["name"].toString()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Owner: ${campsiteDetails["owner"].toString()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description: ${campsiteDetails["description"].toString()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Location: ${campsiteDetails["location"].toString()}")

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Occupancy: ${campsiteDetails["occupancy"].toString()}")
    }
}