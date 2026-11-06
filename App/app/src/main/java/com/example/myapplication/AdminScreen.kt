package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.atomiccryptids.campsiteapp.Campsite
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen for admin
 *
 * Option to view profiles of campers
 *
 * @param navController controller object for navigating between screens
 * @param admin object for user accessing
 */
@Composable
fun AdminScreen(navController: NavController, admin: Admin) {

    var message by remember { mutableStateOf("") }
    var campers by remember { mutableStateOf<List<Camper>?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding().verticalScroll(rememberScrollState())) {

        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                message = ""
                admin.getCampers({ campers = it }, {message = it})
            }
        ) {
            Text("View camper profiles")
        }
        Text(message)

        // Admin can't actually view the camper profile details yet since the attributes are not set up yet

        campers?.forEach { camper ->
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            ) {
                Text("Camper")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}