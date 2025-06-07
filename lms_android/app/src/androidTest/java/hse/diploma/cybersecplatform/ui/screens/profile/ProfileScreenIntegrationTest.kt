package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.di.vm.LocalAuthStateViewModel
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.navigation.Screen
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.screens.isCircularProgressIndicator
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var navController: NavHostController
    private lateinit var authStateViewModel: AuthStateViewModel

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        navController = mockk(relaxed = true)
        authStateViewModel = mockk(relaxed = true)
    }

    private fun getString(resId: Int): String = context.getString(resId)

    @Test
    fun loadingState_displaysLoadingIndicator() {
        every { viewModel.profileState } returns MutableStateFlow(ProfileState.Loading)

        composeRule.setContent {
            CompositionLocalProvider(LocalAuthStateViewModel provides authStateViewModel) {
                ProfileScreenWrapper(profileViewModel = viewModel, navHostController = navController)
            }
        }

        composeRule.onNode(isCircularProgressIndicator())
            .assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorScreen() {
        every { viewModel.profileState } returns
            MutableStateFlow(ProfileState.Error(ErrorType.NoInternet))

        composeRule.setContent {
            CompositionLocalProvider(LocalAuthStateViewModel provides authStateViewModel) {
                ProfileScreenWrapper(profileViewModel = viewModel, navHostController = navController)
            }
        }

        composeRule.onNodeWithText(getString(R.string.no_internet_error))
            .assertIsDisplayed()
    }

    @Test
    fun successState_displaysProfileContent() {
        every { viewModel.profileState } returns
            MutableStateFlow(
                ProfileState.Success(
                    ProfileUiState(userData = testUser),
                ),
            )

        composeRule.setContent {
            CompositionLocalProvider(LocalAuthStateViewModel provides authStateViewModel) {
                ProfileScreenWrapper(profileViewModel = viewModel, navHostController = navController)
            }
        }

        composeRule.onNodeWithText(
            text = context.getString(R.string.welcome_text, testUser.fullName),
            ignoreCase = true,
        ).assertIsDisplayed()

        composeRule.onNodeWithText(
            text = context.getString(R.string.email_label, testUser.email),
            ignoreCase = true,
        ).assertIsDisplayed()
    }

    @Test
    fun menu_navigatesToSettings() {
        every { viewModel.profileState } returns
            MutableStateFlow(
                ProfileState.Success(
                    ProfileUiState(userData = testUser),
                ),
            )

        composeRule.setContent {
            CompositionLocalProvider(LocalAuthStateViewModel provides authStateViewModel) {
                ProfileScreenWrapper(profileViewModel = viewModel, navHostController = navController)
            }
        }

        composeRule.onNodeWithText(getString(R.string.profile_settings_section))
            .performClick()

        verify { navController.navigate(Screen.Settings.route) }
    }

    @Test
    fun logout_callsAuthStateViewModel() {
        every { viewModel.profileState } returns
            MutableStateFlow(
                ProfileState.Success(
                    ProfileUiState(userData = testUser),
                ),
            )

        composeRule.setContent {
            CompositionLocalProvider(LocalAuthStateViewModel provides authStateViewModel) {
                ProfileScreenWrapper(profileViewModel = viewModel, navHostController = navController)
            }
        }

        composeRule.onNodeWithText(getString(R.string.profile_logout))
            .performClick()

        verify { authStateViewModel.logout() }
    }

    companion object {
        private val testUser =
            UserData(
                username = "testuser",
                fullName = "Test User",
                email = "test@example.com",
                null,
            )
    }
}
