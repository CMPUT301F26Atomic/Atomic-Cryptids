package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Screen that displays campsite details
 *
 * Resources:
 * For star rating: https://dev.to/myougatheaxo/star-rating-ui-in-compose-custom-rating-bar-implementation-2hjm
 *
 * @param navController controller used for navigating between screens
 * @param campsite campsite object which the details are for
 * @param user the user viewing the campsite, if camper they can its lottery
 */
@Composable
fun CampsiteScreen(navController: NavController, campsite: Campsite, user: User) {
    var message by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(1) }
    var lotteryLoaded by remember(campsite.id) { mutableStateOf(false) }

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
                lotteryLoaded = true
            }
    }

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
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back")
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = campsite.getName(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = campsite.getLocation(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Capacity",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "${campsite.getCapacity()}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Occupancy",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "${campsite.getOccupancy()}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "About this campsite",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = campsite.getDescription(),
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider()

                Text(
                    text = "Rules",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = campsite.getRules(),
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider()

                Text(
                    text = "Campsite owner",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = campsite.getOwner(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        lotteryDetails?.let { lottery ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Lottery Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

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
                }
            }
        }
        if (lotteryLoaded && lotteryDetails == null) {
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
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No lottery available",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "There is currently no lottery associated with this campsite.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        if (user is Camper && lotteryDetails != null) {
            Button(
                onClick = {
                    user.joinLottery(campsite) { result ->
                        message = result
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Join Lottery")
            }

            OutlinedButton(
                onClick = {
                    user.joinWaitlist(campsite) { result ->
                        message = result
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Join waiting list")
            }

            TextButton(
                onClick = {
                    user.leaveWaitlist(campsite) { result ->
                        message = result
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Leave waiting list")
            }
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (user is Camper) {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(5) { i ->
                    val isFilled = rating > i

                    IconButton(
                        onClick = { rating = i + 1 }
                    ) {
                        Icon(
                            imageVector =
                                if (isFilled) {
                                    Icons.Filled.Star
                                }
                                else {
                                    Icons.Outlined.Star
                                },
                            contentDescription = "Rating ${i + 1}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text("Rating: $rating")

            TextField(
                value = review,
                onValueChange = { review = it },
                label = { Text("Add review") }
            )

            Button(
                onClick = {
                    val reviewText = review

                    user.submitReview(campsite, rating, reviewText)

                    review = ""
                    rating = 1
                }
            ) {
                Text("Submit review")
            }
        }
    }
}
