package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SettingsRepoImplTests {
    private val preferencesManager: AppPreferencesManager = mockk()

    private lateinit var settingsRepo: SettingsRepoImpl

    @Before
    fun setup() {
        every { preferencesManager.themeFlow } returns MutableStateFlow(AppTheme.SYSTEM)
        every { preferencesManager.languageFlow } returns MutableStateFlow(Language.ENGLISH)

        settingsRepo = SettingsRepoImpl(preferencesManager)
    }

    @Test
    fun `when setThemePreference is called, then update theme in preferences manager`() =
        runTest {
            coJustRun { preferencesManager.setTheme(any()) }

            settingsRepo.setThemePreference(AppTheme.DARK)

            verify { preferencesManager.setTheme(AppTheme.DARK) }
        }

    @Test
    fun `when setLanguagePreference is called, then update language in preferences manager`() =
        runTest {
            coJustRun { preferencesManager.setLanguage(any()) }

            settingsRepo.setLanguagePreference(Language.RUSSIAN)

            verify { preferencesManager.setLanguage(Language.RUSSIAN) }
        }
}
