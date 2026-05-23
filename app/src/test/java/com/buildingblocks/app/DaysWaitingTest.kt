package com.buildingblocks.app

import com.buildingblocks.app.domain.model.LegoSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.util.UUID

class DaysWaitingTest {

    private fun setAddedSecondsAgo(seconds: Long) = LegoSet(
        userId = UUID.randomUUID(),
        name = "Test",
        addedAt = Instant.now().minusSeconds(seconds)
    )

    @Test
    fun `set added just now returns 0 days waiting`() {
        val set = setAddedSecondsAgo(0)
        assertEquals(0L, set.daysWaiting())
    }

    @Test
    fun `set added 1 day ago returns 1 day waiting`() {
        val set = setAddedSecondsAgo(86_400)
        assertEquals(1L, set.daysWaiting())
    }

    @Test
    fun `set added 7 days ago returns 7 days waiting`() {
        val set = setAddedSecondsAgo(7 * 86_400)
        assertEquals(7L, set.daysWaiting())
    }

    @Test
    fun `set added 30 days ago returns 30 days waiting`() {
        val set = setAddedSecondsAgo(30 * 86_400)
        assertEquals(30L, set.daysWaiting())
    }

    @Test
    fun `daysWaiting is non-negative`() {
        val set = setAddedSecondsAgo(0)
        assertTrue(set.daysWaiting() >= 0)
    }

    @Test
    fun `set added 23 hours ago returns 0 days (partial day truncated)`() {
        val set = setAddedSecondsAgo(23 * 3_600)
        assertEquals(0L, set.daysWaiting())
    }
}
