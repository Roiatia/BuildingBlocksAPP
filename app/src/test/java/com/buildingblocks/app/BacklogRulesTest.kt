package com.buildingblocks.app

import com.buildingblocks.app.domain.model.SetStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BacklogRulesTest {

    @Test
    fun `SEALED is in backlog`() = assertTrue(SetStatus.SEALED.isBacklog())

    @Test
    fun `UNBUILT is in backlog`() = assertTrue(SetStatus.UNBUILT.isBacklog())

    @Test
    fun `IN_PROGRESS is in backlog`() = assertTrue(SetStatus.IN_PROGRESS.isBacklog())

    @Test
    fun `MISSING_PARTS is in backlog`() = assertTrue(SetStatus.MISSING_PARTS.isBacklog())

    @Test
    fun `WAITING_FOR_DISPLAY_SPACE is in backlog`() = assertTrue(SetStatus.WAITING_FOR_DISPLAY_SPACE.isBacklog())

    @Test
    fun `BUILT is not in backlog`() = assertFalse(SetStatus.BUILT.isBacklog())

    @Test
    fun `DISASSEMBLED is not in backlog`() = assertFalse(SetStatus.DISASSEMBLED.isBacklog())

    @Test
    fun `SOLD is not in backlog`() = assertFalse(SetStatus.SOLD.isBacklog())

    @Test
    fun `every SetStatus is either backlog or not-backlog with no gaps`() {
        val all = SetStatus.entries.toSet()
        val backlog = all.filter { it.isBacklog() }.toSet()
        val nonBacklog = all.filter { !it.isBacklog() }.toSet()
        assertTrue(backlog.isNotEmpty())
        assertTrue(nonBacklog.isNotEmpty())
        assertTrue((backlog + nonBacklog) == all)
    }
}
