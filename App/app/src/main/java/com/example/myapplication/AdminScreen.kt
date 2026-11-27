package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog

/**
 * Screen for admin
 *
 * Option to view profiles of campers
 *
 * @param navController controller object for navigating between screens
 * @param admin object for user accessing
 * @param onSelectCamper Callback invoked when a camper profile is selected.
 * @param onSelectCampsite Callback invoked when a campsite is selected.
 */
@Composable
fun AdminScreen(
    navController: NavController,
    admin: Admin,
    onSelectCamper: (Camper) -> Unit,
    onSelectCampsite: (Campsite) -> Unit
) {

    var message by remember { mutableStateOf("") }
    var campers by remember { mutableStateOf<List<Camper>?>(null) }
    var campsites by remember { mutableStateOf<List<Campsite>?>(null) }
    var profileToDelete by remember { mutableStateOf<Camper?>(null) }
    var campsiteToDelete by remember { mutableStateOf<Campsite?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

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
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        ) {
            Text("Back")
        }

        Text(
            text = "Admin dashboard",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Manage camper profiles and campsite listings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Camper profiles",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Browse registered campers.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        message = ""
                        admin.getCampers(
                            onResult = {
                                campers = it
                                message = "Found ${it.size} camper profiles"
                            },
                            onError = {
                                message = it
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("viewCampers")
                ) {
                    Text("View camper profiles")
                }

                if (message.isNotEmpty()) {
                    Text(message)
                }

                //The following block was written with assistance from OpenAI Chatgpt
                //when prompted with : How do I allow an admin to view camper profile information

                campers?.forEach { camper ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onSelectCamper(camper)
                                    navController.navigate("camper-profile")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("camper")
                            ) {
                                Text(
                                    camper.getName().ifBlank { "Unnamed camper" }
                                )
                            }

                            TextButton(
                                onClick = {
                                    profileToDelete = camper
                                },
                                enabled = !isDeleting
                            ) {
                                Text(
                                    text = "Remove",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Campsites",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Browse campsite listings.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        message = ""
                        admin.searchCampsites({ campsites = it }, {message = it})
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("viewCampsites")
                ) {
                    Text("View campsites")
                }

                if (message.isNotEmpty()) {
                    Text(message)
                }

                campsites?.forEach { campsite ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onSelectCampsite(campsite)
                                        navController.navigate("campsite")
                                    },
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = campsite.getName(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = campsite.getLocation(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            TextButton(
                                onClick = {
                                    campsiteToDelete = campsite
                                },
                                enabled = !isDeleting
                            ) {
                                Text(
                                    text = "Remove",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    profileToDelete?.let { camper ->
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) profileToDelete = null
            },
            title = {
                Text("Remove profile?")
            },
            text = {
                Text(
                    "Are you sure you want to remove ${
                        camper.getName().ifBlank { "this camper" }
                    }?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        isDeleting = true

                        admin.removeCamper(
                            camper = camper,
                            onSuccess = {
                                campers = campers?.filter {
                                    it.id != camper.id
                                }
                                profileToDelete = null
                                isDeleting = false
                                message = "Camper profile removed"
                            },
                            onError = {
                                message = it
                                profileToDelete = null
                                isDeleting = false
                            }
                        )
                    }
                ) {
                    Text(
                        "Remove",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = { profileToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    campsiteToDelete?.let { campsite ->
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) campsiteToDelete = null
            },
            title = {
                Text("Remove campsite?")
            },
            text = {
                Text(
                    "Are you sure you want to remove ${campsite.getName()}?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        isDeleting = true

                        admin.removeCampsite(
                            campsite = campsite,
                            onSuccess = {
                                campsites = campsites?.filter {
                                    it.id != campsite.id
                                }
                                campsiteToDelete = null
                                isDeleting = false
                                message = "Campsite removed"
                            },
                            onError = {
                                message = it
                                campsiteToDelete = null
                                isDeleting = false
                            }
                        )
                    }
                ) {
                    Text(
                        "Remove",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = { campsiteToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}