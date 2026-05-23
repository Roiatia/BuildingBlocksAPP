package com.buildingblocks.app

import com.buildingblocks.app.domain.model.*
import com.buildingblocks.app.domain.usecase.SortSetsUseCase
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.util.UUID

class SetSortingTest {

    private fun makeSet(
        name: String = "Test",
        pieceCount: Int? = null,
        priority: Priority = Priority.MEDIUM,
        difficulty: Difficulty? = null,
        addedAt: Instant = Instant.now()
    ) = LegoSet(
        userId = UUID.randomUUID(),
        name = name,
        pieceCount = pieceCount,
        priority = priority,
        difficulty = difficulty,
        addedAt = addedAt
    )

    private val earlier = Instant.ofEpochSecond(1_000_000)
    private val later   = Instant.ofEpochSecond(2_000_000)

    private val sets = listOf(
        makeSet("Bravo",   pieceCount = 500, priority = Priority.HIGH,   difficulty = Difficulty.ADVANCED, addedAt = earlier),
        makeSet("Alpha",   pieceCount = 200, priority = Priority.LOW,    difficulty = Difficulty.BEGINNER, addedAt = later),
        makeSet("Charlie", pieceCount = 900, priority = Priority.MEDIUM, difficulty = Difficulty.EXPERT,   addedAt = earlier)
    )

    @Test
    fun `DATE_ADDED_DESC puts latest first`() {
        assertEquals("Alpha", SortSetsUseCase(sets, SortOrder.DATE_ADDED_DESC).first().name)
    }

    @Test
    fun `DATE_ADDED_ASC puts earliest first`() {
        assertEquals("Alpha", SortSetsUseCase(sets, SortOrder.DATE_ADDED_ASC).last().name)
    }

    @Test
    fun `NAME_ASC sorts alphabetically`() {
        assertEquals(listOf("Alpha", "Bravo", "Charlie"), SortSetsUseCase(sets, SortOrder.NAME_ASC).map { it.name })
    }

    @Test
    fun `NAME_DESC reverses alphabetical order`() {
        assertEquals(listOf("Charlie", "Bravo", "Alpha"), SortSetsUseCase(sets, SortOrder.NAME_DESC).map { it.name })
    }

    @Test
    fun `PIECE_COUNT_DESC puts highest first`() {
        assertEquals(900, SortSetsUseCase(sets, SortOrder.PIECE_COUNT_DESC).first().pieceCount)
    }

    @Test
    fun `PIECE_COUNT_ASC puts lowest first`() {
        assertEquals(200, SortSetsUseCase(sets, SortOrder.PIECE_COUNT_ASC).first().pieceCount)
    }

    @Test
    fun `PRIORITY_HIGH puts HIGH priority first`() {
        assertEquals(Priority.HIGH, SortSetsUseCase(sets, SortOrder.PRIORITY_HIGH).first().priority)
    }

    @Test
    fun `DIFFICULTY_HIGH puts EXPERT first`() {
        assertEquals(Difficulty.EXPERT, SortSetsUseCase(sets, SortOrder.DIFFICULTY_HIGH).first().difficulty)
    }

    @Test
    fun `all SortOrder values have a non-blank displayName`() {
        SortOrder.entries.forEach { order ->
            assert(order.displayName().isNotBlank()) { "$order has blank displayName" }
        }
    }
}
