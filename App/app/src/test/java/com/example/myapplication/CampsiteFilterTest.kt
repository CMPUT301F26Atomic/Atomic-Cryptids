
package com.example.myapplication

import com.atomiccryptids.campsiteapp.Campsite
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Unit tests for campsite filtering by location, guest capacity,
 * and availability date.
 *
 * Verifies individual filters, date boundaries, and combined filtering.
 */
class CampsiteFilterTest {

    //The following tests were provided by OpenAI, ChatGPT when prompted with
    //"Please provide kotlin tests to test the functionality of the search filtering system"


    /**
     * Creates a campsite with test data for filtering tests.
     *
     * @param name Name of the test campsite.
     * @param location Location of the test campsite.
     * @param capacity Maximum guest capacity.
     * @param startDate Optional inclusive availability start date.
     * @param endDate Optional exclusive availability end date.
     * @return A Campsite object used for testing.
     */
    private fun createTestCampsite(
        name: String,
        location: String,
        capacity: Int,
        startDate: Long? = null,
        endDate: Long? = null
    ): Campsite {
        return Campsite(
            name = name,
            location = location,
            capacity = capacity,
            owner = "test-owner",
            description = "Test campsite",
            availableStartDate = startDate,
            availableEndDate = endDate
        )
    }

    @Test
    fun filterByLocation_returnsOnlyMatchingCampsites() {
        val campsites = listOf(
            createTestCampsite("Site A", "Jasper", 4),
            createTestCampsite("Site B", "Banff", 6),
            createTestCampsite("Site C", "Jasper", 8)
        )

        val result = filterCampsites(
            campsites = campsites,
            location = "Jasper",
            guestCount = null,
            date = null
        )

        assertEquals(2, result.size)
        assertEquals(
            listOf("Site A", "Site C"),
            result.map { it.getName() }
        )
    }

    @Test
    fun filterByGuestCount_returnsCampsitesWithSufficientCapacity() {
        val campsites = listOf(
            createTestCampsite("Site A", "Jasper", 2),
            createTestCampsite("Site B", "Jasper", 5),
            createTestCampsite("Site C", "Banff", 8)
        )

        val result = filterCampsites(
            campsites = campsites,
            location = null,
            guestCount = 5,
            date = null
        )

        assertEquals(2, result.size)
        assertEquals(
            listOf("Site B", "Site C"),
            result.map { it.getName() }
        )
    }

    @Test
    fun filterByDate_returnsOnlyAvailableCampsites() {
        val day = 86_400_000L

        val campsites = listOf(
            createTestCampsite(
                "Site A", "Jasper", 4,
                startDate = 10 * day,
                endDate = 20 * day
            ),
            createTestCampsite(
                "Site B", "Banff", 6,
                startDate = 20 * day,
                endDate = 30 * day
            ),
            createTestCampsite(
                "Site C", "Jasper", 8
            )
        )

        val result = filterCampsites(
            campsites = campsites,
            location = null,
            guestCount = null,
            date = 15 * day
        )

        assertEquals(1, result.size)
        assertEquals("Site A", result[0].getName())
    }

    @Test
    fun filterByDate_respectsAvailabilityBoundaries() {
        val day = 86_400_000L

        val campsite = createTestCampsite(
            name = "Site A",
            location = "Jasper",
            capacity = 5,
            startDate = 10 * day,
            endDate = 20 * day
        )

        val campsites = listOf(campsite)

        // The first available day should be included.
        val startResult = filterCampsites(
            campsites, null, null, 10 * day
        )
        assertEquals(1, startResult.size)

        // The last available day should be included.
        val lastDayResult = filterCampsites(
            campsites, null, null, 19 * day
        )
        assertEquals(1, lastDayResult.size)

        // The exclusive end date should not be included.
        val endResult = filterCampsites(
            campsites, null, null, 20 * day
        )
        assertEquals(0, endResult.size)

        // Dates before availability begins should be excluded.
        val beforeResult = filterCampsites(
            campsites, null, null, 9 * day
        )
        assertEquals(0, beforeResult.size)
    }


    @Test
    fun filterByAllCriteria_returnsOnlyMatchingCampsites() {
        val day = 86_400_000L

        val campsites = listOf(
            createTestCampsite(
                "Site A", "Jasper", 6,
                startDate = 10 * day,
                endDate = 20 * day
            ),
            createTestCampsite(
                "Site B", "Jasper", 2,
                startDate = 10 * day,
                endDate = 20 * day
            ),
            createTestCampsite(
                "Site C", "Banff", 8,
                startDate = 10 * day,
                endDate = 20 * day
            ),
            createTestCampsite(
                "Site D", "Jasper", 8,
                startDate = 20 * day,
                endDate = 30 * day
            )
        )

        val result = filterCampsites(
            campsites = campsites,
            location = "Jasper",
            guestCount = 4,
            date = 15 * day
        )

        assertEquals(1, result.size)
        assertEquals("Site A", result[0].getName())
    }


}
