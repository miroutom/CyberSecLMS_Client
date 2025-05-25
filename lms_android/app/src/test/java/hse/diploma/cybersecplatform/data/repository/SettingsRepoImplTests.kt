package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SettingsRepoImplTests {
    private val apiService: ApiService = mockk()
    private val preferencesManager: AppPreferencesManager = mockk()
    private lateinit var settingsRepo: SettingsRepoImpl

    @Before
    fun setup() {
        every { preferencesManager.themeFlow } returns MutableStateFlow(AppTheme.SYSTEM)
        every { preferencesManager.languageFlow } returns MutableStateFlow(Language.ENGLISH)

        settingsRepo = SettingsRepoImpl(preferencesManager, apiService)
    }

    @Test
    fun `setThemePreference should update theme in preferences manager`() =
        runTest {
            coJustRun { preferencesManager.setTheme(any()) }

            settingsRepo.setThemePreference(AppTheme.DARK)

            verify { preferencesManager.setTheme(AppTheme.DARK) }
        }

    @Test
    fun `setLanguagePreference should update language in preferences manager`() =
        runTest {
            coJustRun { preferencesManager.setLanguage(any()) }

            settingsRepo.setLanguagePreference(Language.RUSSIAN)

            verify { preferencesManager.setLanguage(Language.RUSSIAN) }
        }

    @Test
    fun `initiatePasswordUpdate should return temp token on success`() =
        runTest {
            val mockResponse = mockk<Response<Map<String, String>>> {
                every { isSuccessful } returns true
                every { body() } returns mapOf("tempToken" to "temp_123")
            }
            coEvery { apiService.changePassword(any()) } returns mockResponse

            val result = settingsRepo.initiatePasswordUpdate("old", "new")

            assertTrue(result.isSuccess)
            assertEquals("temp_123", result.getOrNull()?.tempToken)
        }

    @Test
    fun `confirmPasswordUpdate should return success message`() =
        runTest {
            val mockResponse = mockk<Response<LoginResponse>> {
                every { isSuccessful } returns true
                every { body() } returns LoginResponse("token", UserData("u", "f", "e", null))
            }
            coEvery { apiService.verifyOtp(any()) } returns mockResponse

            val result = settingsRepo.confirmPasswordUpdate("123456", "temp_123")

            assertTrue(result.isSuccess)
            assertEquals("Password updated successfully", result.getOrNull()?.message)
        }
}

