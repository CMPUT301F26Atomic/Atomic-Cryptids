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
 * Displays a dropdown menu for selecting a campsite location filter.
 *
 * Allows the user to select a location or clear the filter
 * to display campsites from all locations.
 * Resources:
 *  * https://developer.android.com/develop/ui/compose/components/menu
 *
 * @param menuData List of available campsite locations.
 * @param selectedLocation Currently selected location, or null if no filter is applied.
 * @param onLocationSelected Callback invoked when the user selects or clears a location.
 */
@Composable
fun FilterMenu(
    menuData: List<String>,
    selectedLocation: String?,
    onLocationSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Button(
            onClick = { expanded = !expanded }
        ) {
            Text(selectedLocation ?: "Location")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All locations") },
                onClick = {
                    onLocationSelected(null)
                    expanded = false
                }
            )
            menuData.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onLocationSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}