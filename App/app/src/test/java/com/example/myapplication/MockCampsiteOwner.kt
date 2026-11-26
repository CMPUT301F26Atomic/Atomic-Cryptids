package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite

class MockCampsiteOwner: CampsiteOwner() {

    override fun createCampsite(
        name: String,
        capacity: Int,
        description: String,
        location: String,
        rules: String,
        availableStartDate: Long,
        availableEndDate: Long
    ): Campsite {
        val uid = "1"

        return Campsite(
            name = name,
            capacity = capacity,
            owner = uid,
            description = description,
            location = location,
            rules = rules,
            availableStartDate = availableStartDate,
            availableEndDate = availableEndDate
        )
    }

    override fun postCampsite(campsite: Campsite, onResult: (String) -> Unit) {
        onResult("Saved")
    }

    override fun getOwnedCampsites(onResult: (List<Campsite>) -> Unit, onError: (String) -> Unit) {
        val campsites = emptyList<Campsite>()
        onResult(campsites)
        
        onError("No authenticated user found.")
    }

    override fun updateCampsite(
        campsiteId: String,
        updatedFields: Map<String, Any>,
        onResult: (String) -> Unit
    ) {

    }

    override fun getWaitlist(
        campsite: Campsite,
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        val waitlist = emptyList<String>()
        onResult(waitlist.map { it })

        onError("Error")
    }
}