package com.example.myapplication

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.atomiccryptids.campsiteapp.User

@Composable
fun CommonButtons(user: User, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var notificationsOn by remember { mutableStateOf(false) }

    Button(onClick = onBack) { Text("Back") }
    Button(onClick = {
        notificationsOn = !notificationsOn
        message = user.optNotification(notificationsOn)
    }) { Text("Toggle notifications") }
    Button(onClick = {
        user.deleteProfile()
        onBack()
    }) { Text("Delete profile") }
    Text(message)
}