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
import com.atomiccryptids.campsiteapp.User
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Screen that displays campsite details
 *
 * @param navController controller used for navigating between screens
 * @param campsite campsite object which the details are for
 * @param user the user viewing the campsite, if camper they can its lottery
 */
@Composable
fun CampsiteScreen(navController: NavController, campsite: Campsite, user: User) {
    var message by remember { mutableStateOf("") }

    var lotteryDetails by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    LaunchedEffect(campsite.id) {
        FirebaseFirestore.getInstance()
            .collection("lotteries")
            .whereArrayContains("campsiteIds", campsite.id)
            .get()
            .addOnSuccessListener { snapshot ->
                lotteryDetails = snapshot.documents.firstOrNull()?.data
            }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (user is Camper) {
            Button(onClick = {
                user.joinLottery(campsite) { result ->
                    message = result
                }
            }) {
                Text("Join Lottery")
            }

            Button(onClick = {
                user.joinWaitlist(campsite) { result ->
                    message = result
                }
            }) {
                Text("Join waiting list")
            }

            Button(onClick = {
                user.leaveWaitlist(campsite) { result ->
                    message = result
                }
            }) {
                Text("Leave waiting list")
            }
        }

        Text(message)

        

        Spacer(modifier = Modifier.height(16.dp))


        lotteryDetails?.let { lottery ->
            Text("Lottery Details")

            Text("Number of winners: ${lottery["numberOfWinners"] ?: "N/A"}")

            Text("Lottery capacity: ${lottery["capacity"] ?: "N/A"}")

            val startDate = lottery["startDate"] as? Long
            val endDate = lottery["endDate"] as? Long

            Text(
                "Start date: ${
                    startDate?.let { java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) }
                        ?: "N/A"
                }"
            )

            Text(
                "End date: ${
                    endDate?.let { java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) }
                        ?: "N/A"
                }"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
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
