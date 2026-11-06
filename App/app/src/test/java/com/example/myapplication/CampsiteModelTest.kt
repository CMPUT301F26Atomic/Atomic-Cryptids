package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for campsite attributes, Firestore serialization,
 * and availability date boundaries.
 *
 *
 * The following tests were provided by OpenAI, ChatGPT when prompted with
 * "Please provide test cases for the campsite model"
 */
class CampsiteModelTest {

    private val day = 86_400_000L

    private fun createCampsite(
        start: Long? = 10 * day,
        end: Long? = 20 * day
    ): Campsite {
        return Campsite(
            id = "test-site",
            name = "Pine Lake",
            capacity = 6,
            owner = "test-owner",
            description = "A lakeside campsite",
            location = "Jasper",
            rules = "No fires",
            availableStartDate = start,
            availableEndDate = end
        )
    }

    @Test
    fun campsite_preservesItsAttributes() {
        val campsite = createCampsite()

        assertEquals("test-site", campsite.id)
        assertEquals("Pine Lake", campsite.getName())
        assertEquals("Jasper", campsite.getLocation())
        assertEquals(6, campsite.getCapacity())
        assertEquals("A lakeside campsite", campsite.getDescription())
        assertEquals("No fires", campsite.getRules())
    }

    @Test
    fun campsiteToMap_containsExpectedFields() {
        val campsite = createCampsite()
        val data = campsite.campsiteToMap

        assertEquals("Pine Lake", data["name"])
        assertEquals("Jasper", data["location"])
        assertEquals(6, data["capacity"])
        assertEquals("test-owner", data["owner"])
        assertEquals("No fires", data["rules"])
        assertEquals(10 * day, data["availableStartDate"])
        assertEquals(20 * day, data["availableEndDate"])
    }

    @Test
    fun campsiteToMap_omitsMissingAvailabilityDates() {
        val campsite = createCampsite(
            start = null,
            end = null
        )

        val data = campsite.campsiteToMap

        assertFalse(data.containsKey("availableStartDate"))
        assertFalse(data.containsKey("availableEndDate"))
    }

    @Test
    fun availability_includesStartAndExcludesEnd() {
        val campsite = createCampsite()

        assertFalse(campsite.isAvailableOn(9 * day))
        assertTrue(campsite.isAvailableOn(10 * day))
        assertTrue(campsite.isAvailableOn(15 * day))
        assertFalse(campsite.isAvailableOn(20 * day))
    }

    @Test
    fun availability_returnsFalseWhenDatesAreMissing() {
        val campsite = createCampsite(
            start = null,
            end = null
        )

        assertFalse(campsite.isAvailableOn(15 * day))
    }
}