package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

/**
 * Screen that displays campsite details
 *
 * @param navController controller used for navigating between screens
 * @param campsite campsite object which the details are for
 * @param camper the camper viewing the campsite, used to join its lottery
 */
@Composable
fun CampsiteScreen(navController: NavController, campsite: Campsite, camper: Camper) {
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            camper.joinLottery(campsite)
            message = "Joined Lottery"
        }) { Text("Join Lottery") }

        Text(message)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Capacity: ${campsite.getCapacity()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Name: ${campsite.getName()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Owner: ${campsite.getOwner()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description: ${campsite.getDescription()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Location: ${campsite.getLocation()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Rules: ${campsite.getRules()}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Occupancy: ${campsite.getOccupancy()}")
    }
}
