package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    state: AccountState,
    accounts: AccountController
) {
    var creatingAccount by rememberSaveable {
        mutableStateOf(false)
    }

    var username by rememberSaveable {
        mutableStateOf("")
    }

    var selectedRole by rememberSaveable {
        mutableStateOf("camper")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Atomic Cryptids",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Campsite Management Application",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (creatingAccount) {
                "Create account"
            } else {
                "Log in"
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                accounts.clearError()
            },
            label = {
                Text("Username")
            },
            singleLine = true,
            enabled = !state.busy,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("usernameField")
        )

        // Users choose their role only during registration.
        if (creatingAccount) {
            Text(
                text = "Select your role",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            val roles = listOf(
                "camper" to "Camper",
                "campsite owner" to "Campsite owner",
                "admin" to "Admin"
            )

            roles.forEach { (role, label) ->
                val selected = selectedRole == role

                OutlinedButton(
                    onClick = {
                        selectedRole = role
                        accounts.clearError()
                    },
                    enabled = !state.busy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(
                            when (role) {
                                "camper" -> "camperRoleButton"
                                "campsite owner" -> "ownerRoleButton"
                                else -> "adminRoleButton"
                            }
                        )
                ) {
                    Text(
                        text = if (selected) {
                            "$label (selected)"
                        } else {
                            label
                        }
                    )
                }
            }
        }

        state.error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("loginError")
            )
        }

        if (state.busy) {
            CircularProgressIndicator()
        }

        Button(
            onClick = {
                if (creatingAccount) {
                    accounts.register(
                        username = username,
                        role = selectedRole
                    )
                } else {
                    accounts.login(username)
                }
            },
            enabled = !state.busy && username.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("submitAccountButton")
        ) {
            Text(
                text = if (creatingAccount) {
                    "Create account"
                } else {
                    "Log in"
                }
            )
        }

        TextButton(
            onClick = {
                creatingAccount = !creatingAccount
                accounts.clearError()
            },
            enabled = !state.busy,
            modifier = Modifier.testTag("toggleAccountModeButton")
        ) {
            Text(
                text = if (creatingAccount) {
                    "Already have an account? Log in"
                } else {
                    "New user? Create account"
                }
            )
        }
    }
}