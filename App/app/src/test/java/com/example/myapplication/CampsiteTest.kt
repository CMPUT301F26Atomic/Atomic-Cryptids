package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class CampsiteTest {
    private lateinit var testCampsite: Campsite

    @Before
    fun setUp() {
        testCampsite = Campsite(
            id = "1",
            "Test campsite",
            5,
            "Test owner",
            1,
            "This is a test campsite",
            "Edmonton",
            "No rules",
            null,
            null
        )
    }

    @Test
    fun testGetters() {
        val resultName = testCampsite.getName()
        val resultCapacity = testCampsite.getCapacity()
        val resultOwner = testCampsite.getOwner()
        val resultOccupancy = testCampsite.getOccupancy()
        val resultDescription = testCampsite.getDescription()
        val resultLocation = testCampsite.getLocation()
        val resultRules = testCampsite.getRules()
        val resultStartDate = testCampsite.getAvailableStartDate()
        val resultEndDate = testCampsite.getAvailableEndDate()

        assertEquals("Test campsite", resultName)
        assertEquals(5, resultCapacity)
        assertEquals("Test owner", resultOwner)
        assertEquals(1, resultOccupancy)
        assertEquals("This is a test campsite", resultDescription)
        assertEquals("Edmonton", resultLocation)
        assertEquals("No rules", resultRules)
        assertEquals(null, resultStartDate)
        assertEquals(null, resultEndDate)
    }

    @Test
    fun testIsAvailable() {
        val result = testCampsite.isAvailableOn(0)

        assertEquals(false, result)
    }
}