package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.response.RegisterResponse
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
class RegistrationScreenIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var onNavigateToAuthorization: () -> Unit
    private lateinit var onRegistered: () -> Unit
    private lateinit var onError: (String) -> Unit

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        onNavigateToAuthorization = mockk(relaxed = true)
        onRegistered = mockk(relaxed = true)
        onError = mockk(relaxed = true)

        every { viewModel.isTeacher } returns MutableStateFlow(false)
        every { viewModel.fullName } returns MutableStateFlow(TextFieldValue(""))
        every { viewModel.username } returns MutableStateFlow(TextFieldValue(""))
        every { viewModel.login } returns MutableStateFlow(TextFieldValue(""))
        every { viewModel.password } returns MutableStateFlow(TextFieldValue(""))
        every { viewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(""))
        every { viewModel.isRegistrationEnabled } returns MutableStateFlow(false)
        every { viewModel.isLoading } returns MutableStateFlow(false)
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun shouldDisplayAllRequiredFieldsWhenScreenLoaded() {
        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }

        composeRule.onNodeWithText(getString(R.string.registration_title)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.auth_label_full_name)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.auth_label_username)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.auth_label_email)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.auth_label_password)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.auth_label_confirm_password)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.register_button)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.have_account_button)).assertIsDisplayed()
    }

    @Test
    fun shouldEnableRegisterButtonWhenValidCredentialsEntered() {
        every { viewModel.login } returns MutableStateFlow(TextFieldValue("test@example.com"))
        every { viewModel.password } returns MutableStateFlow(TextFieldValue("Password123"))
        every { viewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue("Password123"))
        every { viewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).assertIsEnabled()
    }

    @Test
    fun shouldDisableRegisterButtonWhenInvalidCredentialsEntered() {
        every { viewModel.login } returns MutableStateFlow(TextFieldValue("invalid-email"))
        every { viewModel.password } returns MutableStateFlow(TextFieldValue("short"))
        every { viewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue("mismatch"))
        every { viewModel.isRegistrationEnabled } returns MutableStateFlow(false)

        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).assertIsNotEnabled()
    }

    @Test
    fun shouldCallRegisterMethodWhenRegisterButtonClickedWithValidData() {
        val testFullName = "John Doe"
        val testUsername = "johndoe"
        val testEmail = "john@example.com"
        val testPassword = "Password123"

        every { viewModel.fullName } returns MutableStateFlow(TextFieldValue(testFullName))
        every { viewModel.username } returns MutableStateFlow(TextFieldValue(testUsername))
        every { viewModel.login } returns MutableStateFlow(TextFieldValue(testEmail))
        every { viewModel.password } returns MutableStateFlow(TextFieldValue(testPassword))
        every { viewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(testPassword))
        every { viewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }
        composeRule.onNodeWithText(getString(R.string.register_button)).performClick()

        verify {
            viewModel.register(
                username = testUsername,
                password = testPassword,
                email = testEmail,
                fullName = testFullName,
                onResult = any(),
            )
        }
    }

    @Test
    fun shouldCallOnRegisteredCallbackWhenRegistrationSucceeds() {
        val testFullName = "John Doe"
        val testUsername = "johndoe"
        val testEmail = "john@example.com"
        val testPassword = "Password123"

        every { viewModel.fullName } returns MutableStateFlow(TextFieldValue(testFullName))
        every { viewModel.username } returns MutableStateFlow(TextFieldValue(testUsername))
        every { viewModel.login } returns MutableStateFlow(TextFieldValue(testEmail))
        every { viewModel.password } returns MutableStateFlow(TextFieldValue(testPassword))
        every { viewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(testPassword))
        every { viewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        val callbackSlot = slot<(Result<RegisterResponse>) -> Unit>()
        every {
            viewModel.register(
                username = any(),
                password = any(),
                email = any(),
                fullName = any(),
                onResult = capture(callbackSlot),
            )
        } answers {
            callbackSlot.captured.invoke(Result.success(RegisterResponse("token", "success")))
        }

        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }
        composeRule.onNodeWithText(getString(R.string.register_button)).performClick()

        verify { onRegistered.invoke() }
    }

    @Test
    fun shouldCallOnErrorCallbackWhenRegistrationFails() {
        val testFullName = "John Doe"
        val testUsername = "johndoe"
        val testEmail = "john@example.com"
        val testPassword = "Password123"
        val errorMessage = "Registration failed"

        every { viewModel.fullName } returns MutableStateFlow(TextFieldValue(testFullName))
        every { viewModel.username } returns MutableStateFlow(TextFieldValue(testUsername))
        every { viewModel.login } returns MutableStateFlow(TextFieldValue(testEmail))
        every { viewModel.password } returns MutableStateFlow(TextFieldValue(testPassword))
        every { viewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(testPassword))
        every { viewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        val callbackSlot = slot<(Result<RegisterResponse>) -> Unit>()
        every {
            viewModel.register(
                username = any(),
                password = any(),
                email = any(),
                fullName = any(),
                onResult = capture(callbackSlot),
            )
        } answers {
            callbackSlot.captured.invoke(Result.failure(Exception(errorMessage)))
        }

        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }
        composeRule.onNodeWithText(getString(R.string.register_button)).performClick()

        verify { onError.invoke(errorMessage) }
    }

    @Test
    fun shouldNavigateToAuthorizationWhenHaveAccountButtonClicked() {
        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }
        composeRule.onNodeWithText(getString(R.string.have_account_button)).performClick()

        verify { onNavigateToAuthorization.invoke() }
    }

    @Test
    fun shouldShowProgressIndicatorWhenLoadingStateActive() {
        every { viewModel.isLoading } returns MutableStateFlow(true)

        composeRule.setContent {
            RegistrationScreenWrapper(
                viewModel = viewModel,
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).assertExists()
    }
}
