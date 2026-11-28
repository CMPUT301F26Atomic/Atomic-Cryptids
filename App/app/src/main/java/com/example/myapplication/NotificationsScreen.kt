package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController

/**
 * Shows the camper whether they won or lost the lotteries they were in.
 *
 * @param navController used to go back to the previous screen
 */
@Composable
fun NotificationsScreen(navController: NavController) {
    var notifications by remember { mutableStateOf(listOf<NotificationItem>()) }
    var notificationStatus by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            notificationStatus = "You are not signed in"
        } else {
            Notification.getNotifications(
                user.uid,
                { result -> notifications = result },
                { error -> notificationStatus = error }
            )
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }

        Text("Notifications", fontSize = 32.sp)
        HorizontalDivider()

        if (notifications.isEmpty()) {
            Text("You have no notifications")
        }

        for (notification in notifications) {
            Text(notification.message, fontSize = 18.sp)
            HorizontalDivider()
        }

        Text(notificationStatus)
    }
}