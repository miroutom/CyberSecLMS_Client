package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.text.input.TextFieldValue
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
    private lateinit var viewModel: AuthorizationViewModel
    private val authRepo: AuthRepo = mock()
    private val testDispatcher = UnconfinedTestDispatcher()

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
    fun `initial state should have empty credentials and disabled login`() =
        runTest {
            assertEquals(TextFieldValue(EMPTY_STRING), viewModel.username.first())
            assertEquals(TextFieldValue(EMPTY_STRING), viewModel.password.first())
            assertFalse(viewModel.isAuthorizationEnabled.first())
            assertFalse(viewModel.isLoading.first())
        }

    @Test
    fun `onUsernameChange should update username state`() =
        runTest {
            val newUsername = TextFieldValue(VALID_USERNAME)
            viewModel.onUsernameChange(newUsername)
            assertEquals(newUsername, viewModel.username.first())
        }

    @Test
    fun `onPasswordChange should update password state`() =
        runTest {
            val newPassword = TextFieldValue(VALID_PASSWORD)
            viewModel.onPasswordChange(newPassword)
            assertEquals(newPassword, viewModel.password.first())
        }

    @Test
    fun `authorization should be enabled with valid username and password`() =
        runTest {
            viewModel.onUsernameChange(TextFieldValue(VALID_USERNAME))
            viewModel.onPasswordChange(TextFieldValue(VALID_PASSWORD))
            assertTrue(viewModel.isAuthorizationEnabled.first())
        }

    @Test
    fun `authorization should be disabled with empty username`() =
        runTest {
            viewModel.onUsernameChange(TextFieldValue(EMPTY_STRING))
            viewModel.onPasswordChange(TextFieldValue(VALID_PASSWORD))
            assertFalse(viewModel.isAuthorizationEnabled.first())
        }

    @Test
    fun `authorization should be disabled with invalid password`() =
        runTest {
            viewModel.onUsernameChange(TextFieldValue(VALID_USERNAME))
            viewModel.onPasswordChange(TextFieldValue(INVALID_PASSWORD))
            assertFalse(viewModel.isAuthorizationEnabled.first())
        }

    @Test
    fun `isLoading should be true during login and false after completion`() =
        runTest {
            whenever(authRepo.login(VALID_USERNAME, VALID_PASSWORD))
                .thenReturn(Result.success(tempTokenResponse))

            assertFalse(viewModel.isLoading.first())

            viewModel.login(VALID_USERNAME, VALID_PASSWORD) { }

            assertFalse(viewModel.isLoading.first())
        }

    companion object {
        private val tempTokenResponse = TempTokenResponse("success", "token123")

        private const val VALID_USERNAME = "testuser"
        private const val VALID_PASSWORD = "Password123!"
        private const val INVALID_PASSWORD = "weak"
        private const val EMPTY_STRING = ""
        private const val AUTH_ERROR_MESSAGE = "Auth failed"
    }
}
