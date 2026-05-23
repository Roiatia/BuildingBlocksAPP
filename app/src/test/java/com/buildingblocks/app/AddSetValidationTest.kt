package com.buildingblocks.app

import com.buildingblocks.app.domain.usecase.ValidateAddSetUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AddSetValidationTest {

    @Test
    fun `blank name and blank number fails`() {
        val result = ValidateAddSetUseCase("", "")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `name only succeeds`() {
        val result = ValidateAddSetUseCase("Millennium Falcon", "")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `set number only succeeds`() {
        val result = ValidateAddSetUseCase("", "75192")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `both name and number succeeds`() {
        val result = ValidateAddSetUseCase("Millennium Falcon", "75192")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `whitespace-only name and blank number fails`() {
        val result = ValidateAddSetUseCase("   ", "")
        assertFalse(result.isValid)
    }

    @Test
    fun `blank name and whitespace-only number fails`() {
        val result = ValidateAddSetUseCase("", "   ")
        assertFalse(result.isValid)
    }
}
