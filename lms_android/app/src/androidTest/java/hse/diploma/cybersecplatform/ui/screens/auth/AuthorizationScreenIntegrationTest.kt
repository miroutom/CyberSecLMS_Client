package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import hse.diploma.cybersecplatform.ui.screens.otp.OtpViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthorizationScreenIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var authViewModel: AuthorizationViewModel
    private lateinit var otpViewModel: OtpViewModel
    private lateinit var onNavigateToRegistration: () -> Unit
    private lateinit var onAuthorized: () -> Unit
    private lateinit var onError: (String) -> Unit

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        authViewModel = mockk(relaxed = true)
        otpViewModel = mockk(relaxed = true)

        onNavigateToRegistration = mockk(relaxed = true)
        onAuthorized = mockk(relaxed = true)
        onError = mockk(relaxed = true)

        val usernameState = MutableStateFlow(TextFieldValue(""))
        val passwordState = MutableStateFlow(TextFieldValue(""))
        val isAuthEnabledState = MutableStateFlow(false)
        val isLoadingState = MutableStateFlow(false)
        val otpIsLoadingState = MutableStateFlow(false)

        every { authViewModel.username } returns usernameState
        every { authViewModel.password } returns passwordState
        every { authViewModel.isAuthorizationEnabled } returns isAuthEnabledState
        every { authViewModel.isLoading } returns isLoadingState
        every { otpViewModel.isLoading } returns otpIsLoadingState
    }

    private fun getString(resId: Int): String {
        return context.getString(resId)
    }

    @Test
    fun enteringCredentialsEnablesLoginButton() {
        val usernameState = MutableStateFlow(TextFieldValue(""))
        val passwordState = MutableStateFlow(TextFieldValue(""))
        val isAuthEnabledState = MutableStateFlow(false)

        every { authViewModel.username } returns usernameState
        every { authViewModel.password } returns passwordState
        every { authViewModel.isAuthorizationEnabled } returns isAuthEnabledState

        composeRule.setContent {
            AuthorizationScreenWrapper(
                onNavigateToRegistration = onNavigateToRegistration,
                onAuthorized = onAuthorized,
                onError = onError,
                viewModel = authViewModel,
                otpViewModel = otpViewModel,
            )
        }

        val loginButton = composeRule.onNodeWithText(getString(R.string.auth_button))
        loginButton.assertIsNotEnabled()

        every { authViewModel.onUsernameChange(any()) } answers {
            usernameState.value = TextFieldValue("user@example.com")
        }

        every { authViewModel.onPasswordChange(any()) } answers {
            passwordState.value = TextFieldValue("password123")
        }

        composeRule.onNodeWithText(getString(R.string.auth_label_username))
            .performTextInput("user@example.com")

        composeRule.onNodeWithText(getString(R.string.auth_label_password))
            .performTextInput("password123")

        isAuthEnabledState.value = true

        loginButton.assertIsEnabled()
    }

    @Test
    fun clickingLoginButtonCallsLoginMethod() {
        val usernameState = MutableStateFlow(TextFieldValue("user@example.com"))
        val passwordState = MutableStateFlow(TextFieldValue("password123"))
        val isAuthEnabledState = MutableStateFlow(true)

        every { authViewModel.username } returns usernameState
        every { authViewModel.password } returns passwordState
        every { authViewModel.isAuthorizationEnabled } returns isAuthEnabledState

        composeRule.setContent {
            AuthorizationScreenWrapper(
                onNavigateToRegistration = onNavigateToRegistration,
                onAuthorized = onAuthorized,
                onError = onError,
                viewModel = authViewModel,
                otpViewModel = otpViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.auth_button))
            .performClick()

        verify {
            authViewModel.login(
                username = "user@example.com",
                password = "password123",
                onResult = any(),
            )
        }
    }

    @Test
    fun clickingRegistrationLinkNavigatesToRegistration() {
        composeRule.setContent {
            AuthorizationScreenWrapper(
                onNavigateToRegistration = onNavigateToRegistration,
                onAuthorized = onAuthorized,
                onError = onError,
                viewModel = authViewModel,
                otpViewModel = otpViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.no_account_button))
            .performClick()

        verify { onNavigateToRegistration.invoke() }
    }

    @Test
    fun successfulLoginShowsOtpDialog() {
        val usernameState = MutableStateFlow(TextFieldValue("user@example.com"))
        val passwordState = MutableStateFlow(TextFieldValue("password123"))
        val isAuthEnabledState = MutableStateFlow(true)

        every { authViewModel.username } returns usernameState
        every { authViewModel.password } returns passwordState
        every { authViewModel.isAuthorizationEnabled } returns isAuthEnabledState

        val callbackSlot = slot<(Result<TempTokenResponse>) -> Unit>()
        every {
            authViewModel.login(
                username = any(),
                password = any(),
                onResult = capture(callbackSlot),
            )
        } answers {
            callbackSlot.captured.invoke(Result.success(TempTokenResponse("Success message", "test-token")))
        }

        composeRule.setContent {
            AuthorizationScreenWrapper(
                onNavigateToRegistration = onNavigateToRegistration,
                onAuthorized = onAuthorized,
                onError = onError,
                viewModel = authViewModel,
                otpViewModel = otpViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.auth_button))
            .performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithText(getString(R.string.otp_dialog_title))
            .assertIsDisplayed()
    }

    @Test
    fun loginErrorCallsOnErrorCallback() {
        val usernameState = MutableStateFlow(TextFieldValue("user@example.com"))
        val passwordState = MutableStateFlow(TextFieldValue("password123"))
        val isAuthEnabledState = MutableStateFlow(true)

        every { authViewModel.username } returns usernameState
        every { authViewModel.password } returns passwordState
        every { authViewModel.isAuthorizationEnabled } returns isAuthEnabledState

        val callbackSlot = slot<(Result<TempTokenResponse>) -> Unit>()
        every {
            authViewModel.login(
                username = any(),
                password = any(),
                onResult = capture(callbackSlot),
            )
        } answers {
            callbackSlot.captured.invoke(Result.failure(Exception("Authentication failed")))
        }

        composeRule.setContent {
            AuthorizationScreenWrapper(
                onNavigateToRegistration = onNavigateToRegistration,
                onAuthorized = onAuthorized,
                onError = onError,
                viewModel = authViewModel,
                otpViewModel = otpViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.auth_button))
            .performClick()

        verify { onError.invoke("Authentication failed") }
    }
}
