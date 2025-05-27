package hse.diploma.cybersecplatform.ui.screens.settings

import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var settingsRepo: SettingsRepo
    private lateinit var userRepo: UserRepo
    private lateinit var authRepo: AuthRepo
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        settingsRepo = mockk(relaxed = true)
        userRepo = mockk(relaxed = true)
        authRepo = mockk(relaxed = true)
        every { settingsRepo.getThemePreference() } returns flowOf(AppTheme.DARK)
        every { settingsRepo.getLanguagePreference() } returns flowOf(Language.RUSSIAN)
        coEvery { userRepo.getUserProfile() } returns Result.success(UserData("u", "f", "e", null))
        viewModel = SettingsViewModel(settingsRepo, userRepo, authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSettings should set theme and language preferences from repository`() =
        runTest {
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(AppTheme.DARK, viewModel.themePreference.value)
            assertEquals(Language.RUSSIAN, viewModel.languagePreference.value)
        }

    @Test
    fun `setThemePreference should call repository setThemePreference method`() =
        runTest {
            coJustRun { settingsRepo.setThemePreference(AppTheme.LIGHT) }
            viewModel.setThemePreference(AppTheme.LIGHT)
            testDispatcher.scheduler.advanceUntilIdle()
        }

    @Test
    fun `setLanguagePreference should call repository setLanguagePreference method`() =
        runTest {
            coJustRun { settingsRepo.setLanguagePreference(Language.ENGLISH) }
            viewModel.setLanguagePreference(Language.ENGLISH)
            testDispatcher.scheduler.advanceUntilIdle()
        }

    @Test
    fun `cancelPasswordOtp should clear password temp token and error fields`() =
        runTest {
            viewModel.cancelPasswordOtp()
            assertNull(viewModel.passwordTempToken.value)
            assertNull(viewModel.passwordOtpError.value)
        }

    @Test
    fun `cancelDeleteOtp should clear delete temp token and error fields`() =
        runTest {
            viewModel.cancelDeleteOtp()
            assertNull(viewModel.deleteTempToken.value)
            assertNull(viewModel.deleteOtpError.value)
        }
}
