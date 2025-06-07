package hse.diploma.cybersecplatform.data.api

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class TokenManagerTests {
    private val context: Context = mockk(relaxed = true)
    private val sharedPreferences: SharedPreferences = mockk(relaxed = true)
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)

    private lateinit var tokenManager: TokenManager

    @Before
    fun setup() {
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor

        tokenManager = TokenManager(context)
    }

    @Test
    fun `when saveToken is called, then token is stored in preferences`() {
        tokenManager.saveToken("test_token")
        verify { editor.putString("auth_token", "test_token") }
    }

    @Test
    fun `when getToken is called, then return stored token`() {
        every { sharedPreferences.getString("auth_token", null) } returns "test_token"
        assertEquals("test_token", tokenManager.getToken())
    }

    @Test
    fun `when clearToken is called, then token is removed from preferences`() {
        tokenManager.clearToken()
        verify { editor.remove("auth_token") }
    }

    @Test
    fun `when hasToken is called with existing token, then return true`() {
        every { sharedPreferences.getString("auth_token", null) } returns "test_token"
        assertTrue(tokenManager.hasToken())
    }

    @Test
    fun `when hasToken is called with null token, then return false`() {
        every { sharedPreferences.getString("auth_token", null) } returns null
        assertFalse(tokenManager.hasToken())
    }
}
