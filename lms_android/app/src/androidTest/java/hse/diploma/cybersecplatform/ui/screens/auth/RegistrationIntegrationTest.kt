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
class RegistrationIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var onNavigateToAuthorization: () -> Unit
    private lateinit var onRegistered: () -> Unit
    private lateinit var onError: (String) -> Unit

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        registrationViewModel = mockk(relaxed = true)
        onNavigateToAuthorization = mockk(relaxed = true)
        onRegistered = mockk(relaxed = true)
        onError = mockk(relaxed = true)

        every { registrationViewModel.fullName } returns MutableStateFlow(TextFieldValue(""))
        every { registrationViewModel.username } returns MutableStateFlow(TextFieldValue(""))
        every { registrationViewModel.login } returns MutableStateFlow(TextFieldValue(""))
        every { registrationViewModel.password } returns MutableStateFlow(TextFieldValue(""))
        every { registrationViewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(""))
        every { registrationViewModel.isRegistrationEnabled } returns MutableStateFlow(false)
        every { registrationViewModel.isLoading } returns MutableStateFlow(false)
    }

    private fun getString(resId: Int): String {
        return context.getString(resId)
    }

    @Test
    fun registrationScreen_displaysAllFields() {
        composeRule.setContent {
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
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
    fun enteringValidCredentials_enablesRegisterButton() {

        every { registrationViewModel.login } returns MutableStateFlow(TextFieldValue("test@example.com"))
        every { registrationViewModel.password } returns MutableStateFlow(TextFieldValue("Password123"))
        every { registrationViewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue("Password123"))
        every { registrationViewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        composeRule.setContent {
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).assertIsEnabled()
    }

    @Test
    fun enteringInvalidCredentials_disablesRegisterButton() {

        every { registrationViewModel.login } returns MutableStateFlow(TextFieldValue("invalid-email"))
        every { registrationViewModel.password } returns MutableStateFlow(TextFieldValue("short"))
        every { registrationViewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue("mismatch"))
        every { registrationViewModel.isRegistrationEnabled } returns MutableStateFlow(false)

        composeRule.setContent {
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).assertIsNotEnabled()
    }

    @Test
    fun clickingRegisterButton_callsRegisterMethod() {

        val testFullName = "John Doe"
        val testUsername = "johndoe"
        val testEmail = "john@example.com"
        val testPassword = "Password123"

        every { registrationViewModel.fullName } returns MutableStateFlow(TextFieldValue(testFullName))
        every { registrationViewModel.username } returns MutableStateFlow(TextFieldValue(testUsername))
        every { registrationViewModel.login } returns MutableStateFlow(TextFieldValue(testEmail))
        every { registrationViewModel.password } returns MutableStateFlow(TextFieldValue(testPassword))
        every { registrationViewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(testPassword))
        every { registrationViewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        composeRule.setContent {
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).performClick()

        verify {
            registrationViewModel.register(
                username = testUsername,
                password = testPassword,
                email = testEmail,
                fullName = testFullName,
                onResult = any(),
            )
        }
    }

    @Test
    fun successfulRegistration_callsOnRegisteredCallback() {

        val testFullName = "John Doe"
        val testUsername = "johndoe"
        val testEmail = "john@example.com"
        val testPassword = "Password123"

        every { registrationViewModel.fullName } returns MutableStateFlow(TextFieldValue(testFullName))
        every { registrationViewModel.username } returns MutableStateFlow(TextFieldValue(testUsername))
        every { registrationViewModel.login } returns MutableStateFlow(TextFieldValue(testEmail))
        every { registrationViewModel.password } returns MutableStateFlow(TextFieldValue(testPassword))
        every { registrationViewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(testPassword))
        every { registrationViewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        val callbackSlot = slot<(Result<RegisterResponse>) -> Unit>()
        every {
            registrationViewModel.register(
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
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).performClick()

        verify { onRegistered.invoke() }
    }

    @Test
    fun failedRegistration_callsOnErrorCallback() {

        val testFullName = "John Doe"
        val testUsername = "johndoe"
        val testEmail = "john@example.com"
        val testPassword = "Password123"
        val errorMessage = "Registration failed"

        every { registrationViewModel.fullName } returns MutableStateFlow(TextFieldValue(testFullName))
        every { registrationViewModel.username } returns MutableStateFlow(TextFieldValue(testUsername))
        every { registrationViewModel.login } returns MutableStateFlow(TextFieldValue(testEmail))
        every { registrationViewModel.password } returns MutableStateFlow(TextFieldValue(testPassword))
        every { registrationViewModel.passwordConfirmation } returns MutableStateFlow(TextFieldValue(testPassword))
        every { registrationViewModel.isRegistrationEnabled } returns MutableStateFlow(true)

        val callbackSlot = slot<(Result<RegisterResponse>) -> Unit>()
        every {
            registrationViewModel.register(
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
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).performClick()

        verify { onError.invoke(errorMessage) }
    }

    @Test
    fun clickingHaveAccountButton_navigatesToAuthorization() {
        composeRule.setContent {
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.have_account_button)).performClick()

        verify { onNavigateToAuthorization.invoke() }
    }

    @Test
    fun loadingState_showsProgressIndicator() {

        every { registrationViewModel.isLoading } returns MutableStateFlow(true)

        composeRule.setContent {
            RegistrationScreen(
                onNavigateToAuthorization = onNavigateToAuthorization,
                onRegistered = onRegistered,
                onError = onError,
                viewModel = registrationViewModel,
            )
        }

        composeRule.onNodeWithText(getString(R.string.register_button)).assertExists()
    }
}
