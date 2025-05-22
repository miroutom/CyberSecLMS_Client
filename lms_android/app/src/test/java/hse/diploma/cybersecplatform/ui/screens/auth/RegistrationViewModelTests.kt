package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.text.input.TextFieldValue
import com.nhaarman.mockitokotlin2.mock
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTests {

    private val authRepo: AuthRepo = mock()
    private lateinit var viewModel: RegistrationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = RegistrationViewModel(authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fields change updates flows`() {
        viewModel.onFullNameChange(TextFieldValue(FULL_NAME))
        viewModel.onUsernameChange(TextFieldValue(USERNAME))
        viewModel.onLoginChange(TextFieldValue(EMAIL))
        viewModel.onPasswordChange(TextFieldValue(PASSWORD))
        viewModel.onConfirmPasswordChange(TextFieldValue(PASSWORD))

        assertEquals(FULL_NAME, viewModel.fullName.value.text)
        assertEquals(USERNAME, viewModel.username.value.text)
        assertEquals(EMAIL, viewModel.login.value.text)
        assertEquals(PASSWORD, viewModel.password.value.text)
        assertEquals(PASSWORD, viewModel.passwordConfirmation.value.text)
    }

    @Test
    fun `isRegistrationEnabled false for invalid email`() = runTest {
        viewModel.onLoginChange(TextFieldValue("invalid-email"))
        viewModel.onPasswordChange(TextFieldValue(PASSWORD))
        viewModel.onConfirmPasswordChange(TextFieldValue(PASSWORD))

        advanceUntilIdle()
        assertFalse(viewModel.isRegistrationEnabled.value)
    }

    @Test
    fun `isRegistrationEnabled false for invalid password`() = runTest {
        viewModel.onLoginChange(TextFieldValue(EMAIL))
        viewModel.onPasswordChange(TextFieldValue("short"))
        viewModel.onConfirmPasswordChange(TextFieldValue("short"))

        advanceUntilIdle()
        assertFalse(viewModel.isRegistrationEnabled.value)
    }

    @Test
    fun `isRegistrationEnabled false for mismatching passwords`() = runTest {
        viewModel.onLoginChange(TextFieldValue(EMAIL))
        viewModel.onPasswordChange(TextFieldValue(PASSWORD))
        viewModel.onConfirmPasswordChange(TextFieldValue("AnotherPass1!"))

        advanceUntilIdle()
        assertFalse(viewModel.isRegistrationEnabled.value)
    }

    @Test
    fun `isRegistrationEnabled true for valid email passwords and match`() = runTest {
        viewModel.onLoginChange(TextFieldValue(EMAIL))
        viewModel.onPasswordChange(TextFieldValue(PASSWORD))
        viewModel.onConfirmPasswordChange(TextFieldValue(PASSWORD))

        advanceUntilIdle()
        assertTrue(viewModel.isRegistrationEnabled.value)
    }

    companion object {
        private const val FULL_NAME = "John Test"
        private const val USERNAME = "johntest"
        private const val EMAIL = "john@example.com"
        private const val PASSWORD = "StrongPass1!"
    }
}

