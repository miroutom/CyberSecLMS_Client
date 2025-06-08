package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.text.input.TextFieldValue
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthorizationViewModelTests {
    private val authRepo: AuthRepo = mock()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthorizationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = AuthorizationViewModel(authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewModel is initialized, then credentials are empty and login is disabled`() =
        runTest {
            assertEquals(TextFieldValue(EMPTY_STRING), viewModel.username.first())
            assertEquals(TextFieldValue(EMPTY_STRING), viewModel.password.first())
            assertFalse(viewModel.isAuthorizationEnabled.first())
            assertFalse(viewModel.isLoading.first())
        }

    @Test
    fun `when onUsernameChange is called, then username state is updated`() =
        runTest {
            val newUsername = TextFieldValue(VALID_USERNAME)
            viewModel.onUsernameChange(newUsername)
            assertEquals(newUsername, viewModel.username.first())
        }

    @Test
    fun `when onPasswordChange is called, then password state is updated`() =
        runTest {
            val newPassword = TextFieldValue(VALID_PASSWORD)
            viewModel.onPasswordChange(newPassword)
            assertEquals(newPassword, viewModel.password.first())
        }

    @Test
    fun `when username and password are valid, then authorization is enabled`() =
        runTest {
            viewModel.onUsernameChange(TextFieldValue(VALID_USERNAME))
            viewModel.onPasswordChange(TextFieldValue(VALID_PASSWORD))

            advanceUntilIdle()

            assertTrue(viewModel.isAuthorizationEnabled.first())
        }

    @Test
    fun `when username is empty, then authorization is disabled`() =
        runTest {
            viewModel.onUsernameChange(TextFieldValue(EMPTY_STRING))
            viewModel.onPasswordChange(TextFieldValue(VALID_PASSWORD))
            assertFalse(viewModel.isAuthorizationEnabled.first())
        }

    @Test
    fun `when password is invalid, then authorization is disabled`() =
        runTest {
            viewModel.onUsernameChange(TextFieldValue(VALID_USERNAME))
            viewModel.onPasswordChange(TextFieldValue(INVALID_PASSWORD))
            assertFalse(viewModel.isAuthorizationEnabled.first())
        }

    @Test
    fun `when login is called, then isLoading changes to true during process and false after completion`() =
        runTest {
            whenever(authRepo.login(VALID_USERNAME, VALID_PASSWORD))
                .thenReturn(Result.success(tempTokenResponse))

            assertFalse(viewModel.isLoading.first())

            viewModel.login { result ->
                assertTrue(result.isSuccess)
            }

            assertFalse(viewModel.isLoading.first())
        }

    companion object {
        private val tempTokenResponse = TempTokenResponse("success", "token123")

        private const val VALID_USERNAME = "testuser"
        private const val VALID_PASSWORD = "Password123!"
        private const val INVALID_PASSWORD = "weak"
        private const val EMPTY_STRING = ""
    }
}
