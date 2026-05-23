package com.buildingblocks.app

import com.buildingblocks.app.domain.model.*
import com.buildingblocks.app.domain.usecase.GetRecommendedSetUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.util.UUID

class RecommendationTest {

    private fun makeSet(
        priority: Priority = Priority.MEDIUM,
        addedAt: Instant = Instant.now(),
        status: SetStatus = SetStatus.SEALED
    ) = LegoSet(
        userId = UUID.randomUUID(),
        name = "Set-${priority.name}-${addedAt.epochSecond}",
        priority = priority,
        addedAt = addedAt,
        status = status
    )

    private val t1 = Instant.ofEpochSecond(1_000)
    private val t2 = Instant.ofEpochSecond(2_000)
    private val t3 = Instant.ofEpochSecond(3_000)

    @Test
    fun `empty list returns null`() {
        assertNull(GetRecommendedSetUseCase(emptyList()))
    }

    @Test
    fun `high-priority set wins over older low-priority set`() {
        val low  = makeSet(Priority.LOW,  addedAt = t1)
        val high = makeSet(Priority.HIGH, addedAt = t3)
        assertEquals(high.name, GetRecommendedSetUseCase(listOf(low, high))?.name)
    }

    @Test
    fun `among equal priority, oldest addedAt wins`() {
        val older = makeSet(Priority.MEDIUM, addedAt = t1)
        val newer = makeSet(Priority.MEDIUM, addedAt = t2)
        assertEquals(older.name, GetRecommendedSetUseCase(listOf(newer, older))?.name)
    }

    @Test
    fun `BUILT sets are excluded`() {
        val built  = makeSet(Priority.HIGH, status = SetStatus.BUILT)
        val sealed = makeSet(Priority.LOW,  status = SetStatus.SEALED)
        assertEquals(sealed.name, GetRecommendedSetUseCase(listOf(built, sealed))?.name)
    }

    @Test
    fun `SOLD sets are excluded`() {
        val sold   = makeSet(Priority.HIGH, status = SetStatus.SOLD)
        val unbuilt = makeSet(Priority.LOW, status = SetStatus.UNBUILT)
        assertEquals(unbuilt.name, GetRecommendedSetUseCase(listOf(sold, unbuilt))?.name)
    }

    @Test
    fun `single backlog set is always returned`() {
        val only = makeSet()
        assertEquals(only.name, GetRecommendedSetUseCase(listOf(only))?.name)
    }

    @Test
    fun `tiebreak on addedAt: earliest of equal-priority wins`() {
        val first  = makeSet(Priority.HIGH, addedAt = t1)
        val second = makeSet(Priority.HIGH, addedAt = t2)
        val third  = makeSet(Priority.HIGH, addedAt = t3)
        assertEquals(first.name, GetRecommendedSetUseCase(listOf(third, second, first))?.name)
    }

    @Test
    fun `soft-deleted sets are excluded`() {
        val deleted = makeSet(Priority.HIGH).copy(deletedAt = Instant.now())
        val active  = makeSet(Priority.LOW)
        assertEquals(active.name, GetRecommendedSetUseCase(listOf(deleted, active))?.name)
    }
}
