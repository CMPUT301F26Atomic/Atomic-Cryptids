package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite
import com.atomiccryptids.campsiteapp.User
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MockUserTest {
    val user: User = MockUser()

    @Test
    fun testSearchCampsites() {
        val expectedCampsites = emptyList<Campsite>()
        val expectedMessage = "Error message"

        var resultCampsites = emptyList<Campsite>()
        var resultMessage = ""

        user.searchCampsites(
            onResult = { resultCampsites = it },
            onError = { resultMessage = it }
        )

        assertEquals(expectedCampsites[0], resultCampsites[0])
        assertEquals(expectedMessage, resultMessage)
    }
}