package hse.diploma.cybersecplatform.ui.screens.settings

import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.mock.mockUser
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private val settingsRepo: SettingsRepo = mockk(relaxed = true)
    private val userRepo: UserRepo = mockk(relaxed = true)
    private val authRepo: AuthRepo = mockk(relaxed = true)

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { settingsRepo.getThemePreference() } returns flowOf(AppTheme.DARK)
        every { settingsRepo.getLanguagePreference() } returns flowOf(Language.RUSSIAN)
        coEvery { userRepo.getUserProfile() } returns Result.success(mockUser)

        viewModel = SettingsViewModel(settingsRepo, authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when settings are loaded, then theme and language preferences should be set from repository`() =
        runTest {
            advanceUntilIdle()

            assertEquals(AppTheme.DARK, viewModel.themePreference.value)
            assertEquals(Language.RUSSIAN, viewModel.languagePreference.value)
        }

    @Test
    fun `when theme preference is set, then repository setThemePreference method should be called`() =
        runTest {
            coJustRun { settingsRepo.setThemePreference(AppTheme.LIGHT) }
            viewModel.setThemePreference(AppTheme.LIGHT)

            advanceUntilIdle()
        }

    @Test
    fun `when language preference is set, then repository setLanguagePreference method should be called`() =
        runTest {
            coJustRun { settingsRepo.setLanguagePreference(Language.ENGLISH) }
            viewModel.setLanguagePreference(Language.ENGLISH)

            advanceUntilIdle()
        }

    @Test
    fun `when delete OTP is canceled, then delete temp token and error fields should be cleared`() =
        runTest {
            viewModel.cancelDeleteOtp()

            assertNull(viewModel.deleteTempToken.value)
            assertNull(viewModel.deleteOtpError.value)
        }

    @Test
    fun `when account deletion initiation fails, then tempToken should not be set`() =
        runTest {
            val error = Exception("Invalid password")
            coEvery { authRepo.requestDeleteAccount(any()) } returns
                Result.failure(error)

            var callbackResult: Result<String>? = null

            viewModel.initiateAccountDeletion("wrong") { result ->
                callbackResult = result
            }
            advanceUntilIdle()

            assertNull(viewModel.deleteTempToken.value)
            assertEquals(error, callbackResult?.exceptionOrNull())
        }

    @Test
    fun `when account deletion is confirmed successfully, then tempToken should be cleared`() =
        runTest {
            coEvery { authRepo.confirmDeleteAccount("123456") } returns
                Result.success(MessageResponse("Account deleted"))

            var callbackResult: Result<String>? = null

            viewModel.confirmAccountDeletion("123456") { result ->
                callbackResult = result
            }
            advanceUntilIdle()

            assertNull(viewModel.deleteTempToken.value)
            assertTrue(callbackResult?.isSuccess ?: false)
        }
}
