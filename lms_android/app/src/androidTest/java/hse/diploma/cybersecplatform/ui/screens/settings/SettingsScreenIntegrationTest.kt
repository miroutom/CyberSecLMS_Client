package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var authStateViewModel: AuthStateViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        viewModel = mockk()
        authStateViewModel = mockk()

        every { viewModel.isLoading } returns MutableStateFlow(false)
        every { viewModel.themePreference } returns MutableStateFlow(AppTheme.SYSTEM)
        every { viewModel.languagePreference } returns MutableStateFlow(Language.ENGLISH)
        every { viewModel.user } returns
            MutableStateFlow(
                UserData(
                    username = "testuser",
                    fullName = "Test User",
                    email = "test@example.com",
                    profileImage = null,
                ),
            )
        every { viewModel.passwordTempToken } returns MutableStateFlow(null)
        every { viewModel.deleteTempToken } returns MutableStateFlow(null)
        every { viewModel.passwordOtpError } returns MutableStateFlow(null)
        every { viewModel.deleteOtpError } returns MutableStateFlow(null)
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun settingsMenu_displaysAllOptions() {
        composeRule.setContent {
            SettingsScreen(viewModel = viewModel, authStateViewModel = authStateViewModel)
        }

        composeRule.onNodeWithText(getString(R.string.theme_setting))
            .assertExists()
        composeRule.onNodeWithText(getString(R.string.language_setting))
            .assertExists()
        composeRule.onNodeWithText(getString(R.string.update_password_setting))
            .assertExists()
        composeRule.onNodeWithText(getString(R.string.delete_account))
            .assertExists()
    }

    @Test
    fun themeDialog_displaysWhenThemeOptionClicked() {
        composeRule.setContent {
            SettingsScreen(viewModel = viewModel, authStateViewModel = authStateViewModel)
        }

        composeRule.onNodeWithText(getString(R.string.theme_setting))
            .performClick()

        composeRule.onNode(isDialog())
            .assertExists()
    }

    @Test
    fun languageDialog_displaysWhenLanguageOptionClicked() {
        composeRule.setContent {
            SettingsScreen(viewModel = viewModel, authStateViewModel = authStateViewModel)
        }

        composeRule.onNodeWithText(getString(R.string.language_setting))
            .performClick()

        composeRule.onNode(isDialog())
            .assertExists()
    }

    @Test
    fun passwordDialog_displaysWhenPasswordOptionClicked() {
        composeRule.setContent {
            SettingsScreen(viewModel = viewModel, authStateViewModel = authStateViewModel)
        }

        composeRule.onNodeWithText(getString(R.string.update_password_setting))
            .performClick()

        composeRule.onNode(isDialog())
            .assertExists()
    }

    @Test
    fun deleteDialog_displaysWhenDeleteOptionClicked() {
        composeRule.setContent {
            SettingsScreen(viewModel = viewModel, authStateViewModel = authStateViewModel)
        }

        composeRule.onNodeWithText(getString(R.string.delete_account))
            .performClick()

        composeRule.onNode(isDialog())
            .assertExists()
    }
}
