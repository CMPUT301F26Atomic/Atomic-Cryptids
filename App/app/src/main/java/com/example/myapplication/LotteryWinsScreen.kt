package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

//US 01.04.02 Choose which campsite to reserve
//US 01.04.03 Decline reservation

@Composable
fun LotteryWinsScreen(navController: NavController, onSelectCampsite: (Campsite) -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var wins by remember { mutableStateOf<List<Pair<String, Campsite>>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var refresh by remember { mutableIntStateOf(0) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        LotteryDraw.runDraws { refresh++ }
    }

    LaunchedEffect(refresh) {
        db.collection("lotteries")
            .whereArrayContains("winners", uid)
            .get()
            .addOnSuccessListener { snap ->
                val pairs = snap.documents.flatMap { doc ->
                    (doc.get("campsiteIds") as? List<*>)
                        .orEmpty()
                        .filterIsInstance<String>()
                        .map { campsiteId -> doc.id to campsiteId }
                }
                if (pairs.isEmpty()) {
                    wins = emptyList()
                    loaded = true
                    return@addOnSuccessListener
                }
                val results = mutableListOf<Pair<String, Campsite>>()
                var remaining = pairs.size
                pairs.forEach { (lotteryId, campsiteId) ->
                    db.collection("campsites").document(campsiteId).get()
                        .addOnSuccessListener { doc ->
                            Campsite.fromDoc(doc)?.let { results.add(lotteryId to it) }
                        }
                        .addOnCompleteListener {
                            remaining--
                            if (remaining == 0) {
                                wins = results.toList()
                                loaded = true
                            }
                        }
                }
            }
            .addOnFailureListener { status = "Error: ${it.message}" }
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
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back")
        }

        Text(
            text = "Lotteries you won",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Choose a campsite to reserve or decline your lottery win.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (loaded && wins.isEmpty()) {
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
                        text = "No lottery wins yet.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        wins.forEach { (lotteryId, campsite) ->
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = campsite.getName(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = campsite.getLocation(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                onSelectCampsite(campsite)
                                navController.navigate("reserve")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reserve")
                        }

                        OutlinedButton(
                            onClick = {
                                db.collection("lotteries").document(lotteryId)
                                    .update(
                                        mapOf(
                                            "winners" to FieldValue.arrayRemove(uid),
                                            "declined" to FieldValue.arrayUnion(uid),
                                        )
                                    )
                                    .addOnSuccessListener {
                                        status = "Declined ${campsite.getName()}"
                                        refresh++
                                    }
                                    .addOnFailureListener { status = "Error: ${it.message}" }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Decline")
                        }
                    }
                }
            }
        }

        if (status.isNotEmpty()) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}