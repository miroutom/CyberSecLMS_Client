package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.state.screen_state.SettingsScreenState
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState

@Composable
fun SettingsScreenWrapper(
    viewModel: SettingsViewModel = viewModel(factory = LocalViewModelFactory.current),
    profileViewModel: ProfileViewModel = viewModel(factory = LocalViewModelFactory.current),
    authStateViewModel: AuthStateViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.isLoading.collectAsState()
    val theme by viewModel.themePreference.collectAsState()
    val language by viewModel.languagePreference.collectAsState()
    val deleteTempToken by viewModel.deleteTempToken.collectAsState()
    val deleteOtpError by viewModel.deleteOtpError.collectAsState()
    val profileState by profileViewModel.profileState.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val successMessage by remember { mutableStateOf<String?>(null) }

    val screenState =
        when (profileState) {
            is ProfileState.Success ->
                SettingsScreenState(
                    isLoading = state,
                    theme = theme,
                    language = language,
                    user = (profileState as ProfileState.Success).uiState.userData,
                    deleteTempToken = deleteTempToken,
                    deleteOtpError = deleteOtpError ?: errorMessage,
                    errorMessage = errorMessage,
                    successMessage = successMessage,
                )
            else ->
                SettingsScreenState(
                    isLoading = state,
                    theme = theme,
                    language = language,
                )
        }

    SettingsScreen(
        state = screenState,
        onThemeSelected = viewModel::setThemePreference,
        onLanguageSelected = viewModel::setLanguagePreference,
        onPasswordChangeInitiated = viewModel::initiatePasswordChange,
        onDeleteAccountInitiated = viewModel::initiateAccountDeletion,
        onDeleteOtpSubmitted = viewModel::confirmAccountDeletion,
        onDeleteOtpDismissed = viewModel::cancelDeleteOtp,
        onErrorDismissed = { errorMessage = null },
        onLogout = authStateViewModel::logout,
        modifier = modifier,
    )
}
