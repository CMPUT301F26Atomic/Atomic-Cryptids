package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MockCampsiteOwnerTest {
    val mockOwner: CampsiteOwner = MockCampsiteOwner()
    val testCampsite = Campsite(
        name = "Mock campsite",
        capacity = 1,
        owner = "1",
        description = "This is a mock campsite",
        rules = "No rules",
        location = "Nowhere",
        availableStartDate = 1,
        availableEndDate = 1
    )

    @Test
    fun testCreateCampsite() {
        val resultCampsite = mockOwner.createCampsite("Mock campsite",
            1,
            "This is a mock campsite",
            "Nowhere",
            "No rules",
            1,
            1
        )

        assertEquals(testCampsite, resultCampsite)
    }

    @Test
    fun testPostCampsite() {

        var status = ""

        mockOwner.postCampsite(testCampsite) { result -> status = result }

        assertEquals("Saved", status)
    }

    @Test
    fun testGetOwnedCampsites() {
        var campsites = emptyList<Campsite>()
        var error = ""

        mockOwner.getOwnedCampsites({ result -> campsites = result }, { result -> error = result })

        assertEquals(emptyList<Campsite>(), campsites)
        assertEquals("No authenticated user found.", error)
    }

    @Test
    fun testGetWaitlist() {
        var waitlist = emptyList<String>()
        var error = ""

        mockOwner.getWaitlist(testCampsite, { result -> waitlist = result }, { result -> error = result })

        assertEquals(emptyList<String>(), waitlist)
        assertEquals("Error", error)
    }
}