package hse.diploma.cybersecplatform.utils

import org.junit.Assert.*
import org.junit.Test

class UtilsTest {

    @Test
    fun `isPasswordValid returns true for strong password`() {
        assertTrue(isPasswordValid("Strong1!"))
        assertTrue(isPasswordValid("P@ssw0rd123"))
    }

    @Test
    fun `isPasswordValid returns false for missing uppercase`() {
        assertFalse(isPasswordValid("weak1!password"))
    }

    @Test
    fun `isPasswordValid returns false for missing lowercase`() {
        assertFalse(isPasswordValid("PASSWORD1!"))
    }

    @Test
    fun `isPasswordValid returns false for missing digit`() {
        assertFalse(isPasswordValid("Password!"))
    }

    @Test
    fun `isPasswordValid returns false for missing symbol`() {
        assertFalse(isPasswordValid("Password1"))
    }

    @Test
    fun `isEmailValid returns true for valid email`() {
        assertTrue(isEmailValid("user@example.com"))
    }

    @Test
    fun `isEmailValid returns false for invalid email`() {
        assertFalse(isEmailValid("user@.com"))
        assertFalse(isEmailValid("user.com"))
    }

    @Test
    fun `maskEmail masks short email correctly`() {
        val result = maskEmail("ab@example.com")
        assertEquals("a*@example.com", result)
    }

    @Test
    fun `getPasswordError detects short password`() {
        assertEquals(PasswordError.LENGTH, getPasswordError("aA1!"))
    }

    @Test
    fun `getPasswordError detects missing numbers`() {
        assertEquals(PasswordError.NO_NUMBERS, getPasswordError("Password!"))
    }

    @Test
    fun `getPasswordError detects missing lowercase`() {
        assertEquals(PasswordError.NO_LOWERCASE, getPasswordError("PASSWORD1!"))
    }

    @Test
    fun `getPasswordError detects missing uppercase`() {
        assertEquals(PasswordError.NO_UPPERCASE, getPasswordError("password1!"))
    }

    @Test
    fun `getPasswordError detects missing symbols`() {
        assertEquals(PasswordError.NO_SYMBOLS, getPasswordError("Password1"))
    }

    @Test
    fun `getPasswordError returns NONE for valid password`() {
        assertEquals(PasswordError.NONE, getPasswordError("Valid1!pass"))
    }
}
