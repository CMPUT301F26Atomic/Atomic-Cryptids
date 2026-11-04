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

/**
 * Screen that displays campsite details
 *
 * @param navController controller used for navigating between screens
 * @param campsite campsite object which the details are for
 */
@Composable
fun CampsiteScreen(navController: NavController, campsite: Campsite) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Capacity: ${campsite.getCapacity()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Name: ${campsite.getName()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Owner: ${campsite.getName()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description: ${campsite.getDescription()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Location: ${campsite.getLocation()}")

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Occupancy: ${campsite.getOccupancy()}")
    }
}