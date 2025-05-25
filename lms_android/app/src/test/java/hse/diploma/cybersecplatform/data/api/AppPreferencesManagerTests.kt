package hse.diploma.cybersecplatform.data.api

import android.content.Context
import android.content.SharedPreferences
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AppPreferencesManagerTests {
    private val context: Context = mockk(relaxed = true)
    private val sharedPreferences: SharedPreferences = mockk(relaxed = true)
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)

    private lateinit var prefsManager: AppPreferencesManager

    @Before
    fun setup() {
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor

        prefsManager = AppPreferencesManager(context)
    }

    @Test
    fun `isFirstLaunch should return true by default`() {
        every { sharedPreferences.getBoolean(any(), true) } returns true
        assertTrue(prefsManager.isFirstLaunch())
    }

    @Test
    fun `markAppLaunched should set first launch to false`() {
        prefsManager.markAppLaunched()
        verify { editor.putBoolean("is_first_launch", false) }
    }

    @Test
    fun `setTheme should update theme in prefs and flow`() =
        runTest {
            every { sharedPreferences.getInt(any(), any()) } returns AppTheme.DARK.ordinal

            prefsManager.setTheme(AppTheme.LIGHT)

            verify { editor.putInt("app_theme", AppTheme.LIGHT.ordinal) }
            assertEquals(AppTheme.LIGHT, prefsManager.themeFlow.value)
        }

    @Test
    fun `getTheme should return SYSTEM when no value stored`() {
        every { sharedPreferences.getInt(any(), any()) } returns -1
        assertEquals(AppTheme.SYSTEM, prefsManager.getTheme())
    }

    @Test
    fun `setLanguage should update language in prefs and flow`() =
        runTest {
            every { sharedPreferences.getInt(any(), any()) } returns Language.ENGLISH.ordinal

            prefsManager.setLanguage(Language.RUSSIAN)

            verify { editor.putInt("app_language", Language.RUSSIAN.ordinal) }
            assertEquals(Language.RUSSIAN, prefsManager.languageFlow.value)
        }
}

