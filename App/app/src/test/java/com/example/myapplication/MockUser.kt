package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User

class MockUser: User {
    override val role = "camper"

    override fun searchCampsites(onResult: (List<Campsite>) -> Unit, onError: (String) -> Unit) {
        val campsites = emptyList<Campsite>()

        onResult(campsites)

        onError("Error message")
    }
}