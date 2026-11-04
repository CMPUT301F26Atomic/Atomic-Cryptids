package com.example.myapplication

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


/*
moved the toggle notification and delete profile to a profile setting view function
todo create a settign button in camperScreen that directs user to here
 */
@Composable
fun CamperProfileSetting(
    camper: Camper,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    var notificationsOn by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onBack) { Text("Back") }
// check to see if its changed in firebase
        Button(onClick = {
            notificationsOn = !notificationsOn
            message = camper.optNotification(notificationsOn)
        }) { Text("Toggle notifications") }

        Button(onClick = {
            camper.deleteProfile()
            onDeleted()
        }) { Text("Delete profile") }

        AppPolicyLink()

        Text(message)
    }
}

@Composable
fun AppPolicyLink() {
    val uriHandler = LocalUriHandler.current
    Button(onClick = { uriHandler.openUri("https://sepahvandsepehr.github.io/app-policy/") })
    { Text("App Policy") }
}

@Preview(showBackground = true)
@Composable
fun CamperProfileSettingPreview() {
    CamperProfileSetting(
        camper = Camper(),
        onBack = {},
        onDeleted = {}
    )
}