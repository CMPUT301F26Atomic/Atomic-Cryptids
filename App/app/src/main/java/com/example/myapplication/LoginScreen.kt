package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(state: AccountState, accounts: AccountController) {
    var creating by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("camper") }
    Column(
        Modifier.fillMaxSize()
            .safeDrawingPadding().
            imePadding()
            .verticalScroll
                (rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            if (creating) "Create account"
            else "Log in",
            style = MaterialTheme.typography.headlineMedium)
        Text("Your account is linked to this app's Firebase UID.")
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it;
                accounts.clearError() },

            label = { Text("Username") }, singleLine = true,
            enabled = !state.busy, modifier = Modifier.fillMaxWidth()
        )
        if (creating) {
            Text("Choose your role")
            AccountPolicy.roles.forEach {
                option ->
                Row {
                    RadioButton(
                        selected = role == option,
                        onClick = { role = option; accounts.clearError() },
                        enabled = !state.busy)
                    TextButton(
                        onClick = {
                            role = option;
                            accounts.clearError() },
                        enabled = !state.busy) {
                        Text(
                            when (option) {
                            "camper" -> "Camper"
                            "admin" -> "Admin"
                            else -> "Campsite owner"
                        })
                    }
                }
            }
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        if (state.busy) CircularProgressIndicator()
        Button(
            enabled = !state.busy && username.isNotBlank(),
            onClick = {
                if (creating) accounts.register(username, role)
                else accounts.login(username)
            }, modifier = Modifier.fillMaxWidth()
        ) { Text(if (creating) "Create account"
        else "Log in") }
        TextButton(enabled = !state.busy, onClick = {
            creating = !creating
            accounts.clearError()
        }) {
            Text(if (creating)
                "Already registered? Log in"
            else
                "Create a new account") }
    }
}
