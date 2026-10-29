package com.example.myapplication

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.atomiccryptids.campsiteapp.User

@Composable
fun LoginScreen(onPick: (User) -> Unit) {
    Text("Pick your role")
    Button(onClick = { onPick(CampsiteOwner().apply { saveUser() }) }) { Text("Campsite owner") }
    Button(onClick = { onPick(Camper().apply { saveUser() }) }) { Text("Camper") }
}