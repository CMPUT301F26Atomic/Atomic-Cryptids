package com.example.myapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * UI element for filtering one attribute
 * i.e. location gets one FilterMenu, tags get one FilterMenu
 *
 * Resources:
 * https://developer.android.com/develop/ui/compose/components/menu
 *
 * @param menuData a list of attribute values e.g. for location, get a list of all locations in the database
 * @param filters values chosen to be filtered for
 */
@Composable
fun FilterMenu(
    menuData: List<String>,
    filters: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Button(
            onClick = { expanded = !expanded }
        ) {
            Text("Location")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuData.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { /* TODO implement */ }
                )
            }
        }
    }
}