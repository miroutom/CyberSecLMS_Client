package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.di.vm.LocalAuthStateViewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.state.screen_state.SettingsScreenState

@Composable
fun SettingsScreenWrapper(
    viewModel: SettingsViewModel = viewModel(factory = LocalViewModelFactory.current),
    authStateViewModel: AuthStateViewModel = LocalAuthStateViewModel.current,
    modifier: Modifier = Modifier,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val theme by viewModel.themePreference.collectAsState()
    val language by viewModel.languagePreference.collectAsState()
    val user by viewModel.user.collectAsState()
    val passwordTempToken by viewModel.passwordTempToken.collectAsState()
    val deleteTempToken by viewModel.deleteTempToken.collectAsState()
    val passwordOtpError by viewModel.passwordOtpError.collectAsState()
    val deleteOtpError by viewModel.deleteOtpError.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val state =
        SettingsScreenState(
            isLoading = isLoading,
            theme = theme,
            language = language,
            user = user,
            passwordTempToken = passwordTempToken,
            deleteTempToken = deleteTempToken,
            passwordOtpError = passwordOtpError,
            deleteOtpError = deleteOtpError,
            errorMessage = errorMessage,
            successMessage = successMessage,
        )

    SettingsScreen(
        state = state,
        onThemeSelected = viewModel::setThemePreference,
        onLanguageSelected = viewModel::setLanguagePreference,
        onPasswordChangeInitiated = viewModel::initiatePasswordChange,
        onDeleteAccountInitiated = viewModel::initiateAccountDeletion,
        onPasswordOtpSubmitted = viewModel::verifyPasswordOtp,
        onDeleteOtpSubmitted = viewModel::confirmAccountDeletion,
        onPasswordOtpDismissed = viewModel::cancelPasswordOtp,
        onDeleteOtpDismissed = viewModel::cancelDeleteOtp,
        onErrorDismissed = { errorMessage = null },
        onLogout = authStateViewModel::logout,
        modifier = modifier,
    )
}
