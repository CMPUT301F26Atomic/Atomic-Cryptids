package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atomiccryptids.campsiteapp.Campsite

/**
 * US 02.09.01 Shows the people on the waiting list for a campsite's lottery.
 * The list is loaded when the screen opens.
 *
 * @param campsite The campsite whose lottery waiting list is shown.
 * @param owner The owner viewing the waiting list.
 * @param onBack Callback invoked when the owner returns to the previous screen.
 */
@Composable
fun OwnerWaitlistScreen(
    campsite: Campsite,
    owner: CampsiteOwner,
    onBack: () -> Unit
) {
    var waitlist by remember { mutableStateOf<List<String>>(emptyList()) }
    var waitlistStatus by remember { mutableStateOf("") }

    LaunchedEffect(campsite.id) {
        owner.getWaitlist(
            campsite,
            { waitlist = it },
            { waitlistStatus = it },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Text(text = "Waiting List", fontSize = 32.sp)
        Text(campsite.getName(), fontSize = 20.sp)

        HorizontalDivider()

        Text("People on waiting list: ${waitlist.size}", fontSize = 24.sp)
        if (waitlist.isEmpty()) {
            Text("No one is on the waiting list.")
        }
        for (uid in waitlist) {
            Text(uid)
        }
        Text(waitlistStatus)
    }
}