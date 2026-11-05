package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite

/**
 * Filters campsites based on location, guest capacity, and availability date.
 *
 * Campsites must satisfy all specified criteria to be included.
 * Null filter values indicate that the corresponding filter is not applied.
 *
 * @param campsites List of campsites to filter.
 * @param location Selected location, or null for all locations.
 * @param guestCount Minimum required campsite capacity, or null for any capacity.
 * @param date Selected availability date in UTC milliseconds, or null for any date.
 * @return List of campsites matching all specified filters.
 */
fun filterCampsites(
    campsites: List<Campsite>,
    location: String?,
    guestCount: Int?,
    date: Long?
): List<Campsite> {
    return campsites.filter { campsite ->
        val matchesLocation = location == null ||
                campsite.getLocation().equals(location, ignoreCase = true)

        val matchesCapacity = guestCount == null ||
                campsite.getCapacity() >= guestCount
        val matchesDate = date == null ||
                campsite.isAvailableOn(date)

        matchesLocation && matchesCapacity && matchesDate
    }
}

class CampsiteFilter {
}