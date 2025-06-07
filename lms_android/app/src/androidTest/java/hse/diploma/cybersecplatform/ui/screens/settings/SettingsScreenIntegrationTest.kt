package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.di.vm.LocalAuthStateViewModel
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.mock.mockStats
import hse.diploma.cybersecplatform.mock.mockUser
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.screens.isCircularProgressIndicator
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileUiState
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
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

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authStateViewModel: AuthStateViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        settingsViewModel = mockk(relaxed = true)
        profileViewModel = mockk(relaxed = true)
        authStateViewModel = mockk(relaxed = true)

        every { settingsViewModel.isLoading } returns MutableStateFlow(false)
        every { settingsViewModel.themePreference } returns MutableStateFlow(AppTheme.SYSTEM)
        every { settingsViewModel.languagePreference } returns MutableStateFlow(Language.ENGLISH)
        every { settingsViewModel.deleteTempToken } returns MutableStateFlow(null)
        every { settingsViewModel.deleteOtpError } returns MutableStateFlow(null)
        every { profileViewModel.profileState } returns
            MutableStateFlow(
                ProfileState.Success(
                    ProfileUiState(userData = mockUser, stats = mockStats),
                ),
            )
    }

    private fun getString(resId: Int): String = context.getString(resId)

    private fun setContent() {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalAuthStateViewModel provides authStateViewModel,
            ) {
                SettingsScreenWrapper(
                    viewModel = settingsViewModel,
                    profileViewModel = profileViewModel,
                    authStateViewModel = authStateViewModel,
                )
            }
        }
    }

    @Test
    fun loadingState_displaysLoadingIndicator() {
        every { settingsViewModel.isLoading } returns MutableStateFlow(true)
        setContent()

        composeRule.onNode(isCircularProgressIndicator())
            .assertIsDisplayed()
    }

    @Test
    fun settingsMenu_displaysAllOptions() {
        setContent()

        composeRule.onNodeWithText(getString(R.string.theme_setting))
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.language_setting))
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.update_password_setting))
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.delete_account))
            .assertIsDisplayed()
    }

    @Test
    fun themeDialog_displaysWhenThemeOptionClicked() {
        setContent()

        composeRule.onNodeWithText(getString(R.string.theme_setting))
            .performClick()

        composeRule.onNode(isDialog())
            .assertIsDisplayed()
    }

    @Test
    fun languageDialog_displaysWhenLanguageOptionClicked() {
        setContent()

        composeRule.onNodeWithText(getString(R.string.language_setting))
            .performClick()

        composeRule.onNode(isDialog())
            .assertIsDisplayed()
    }

    @Test
    fun passwordDialog_displaysWhenPasswordOptionClicked() {
        setContent()

        composeRule.onNodeWithText(getString(R.string.update_password_setting))
            .performClick()

        composeRule.onNode(isDialog())
            .assertIsDisplayed()
    }

    @Test
    fun deleteDialog_displaysWhenDeleteOptionClicked() {
        setContent()

        composeRule.onNodeWithText(getString(R.string.delete_account))
            .performClick()

        composeRule.onNode(isDialog())
            .assertIsDisplayed()
    }
}
