package com.example.myapplication

import android.R.id.message
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atomiccryptids.campsiteapp.Campsite


// upon click user sees description of the campsite detail. no specific attribute. paragraph of description
@Composable
fun showCampsiteDetails (camper: Camper, campsite: Campsite,onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onBack) { Text("Back") }
        Button(onClick = { camper.joinLottery(campsite)
        message = "Joined Lottery"}) { Text("Join Lottery") }
        Text(message)
        Text(campsite.description)
    }
}

