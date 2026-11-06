package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Lotteries you won", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (loaded && wins.isEmpty()) {
            Text("No lottery wins yet.")
        }

        wins.forEach { (lotteryId, campsite) ->
            Text("${campsite.getName()} - ${campsite.getLocation()}")
            Row {
                Button(onClick = {
                    onSelectCampsite(campsite)
                    navController.navigate("reserve")
                }) { Text("Reserve") }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(onClick = {
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
                }) { Text("Decline") }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(status)
    }
}
