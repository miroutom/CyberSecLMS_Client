package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.di.vm.LocalAuthStateViewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.components.dialogs.DeleteAccountDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.ErrorDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.LanguageChooserDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.OtpDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.PasswordChangeDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.ThemeChooserDialog
import hse.diploma.cybersecplatform.ui.components.menu.SettingsDialog
import hse.diploma.cybersecplatform.ui.components.menu.SettingsMenu
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.maskEmail
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val viewModel: SettingsViewModel = viewModel(factory = LocalViewModelFactory.current)
    val authStateViewModel: AuthStateViewModel = LocalAuthStateViewModel.current

    val isLoading by viewModel.isLoading.collectAsState()
    val theme by viewModel.themePreference.collectAsState()
    val language by viewModel.languagePreference.collectAsState()
    val user by viewModel.user.collectAsState()

    val passwordTempToken by viewModel.passwordTempToken.collectAsState()
    val deleteTempToken by viewModel.deleteTempToken.collectAsState()
    val passwordOtpError by viewModel.passwordOtpError.collectAsState()
    val deleteOtpError by viewModel.deleteOtpError.collectAsState()

    var visibleDialog by remember { mutableStateOf(SettingsDialog.NONE) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(passwordTempToken) {
        if (passwordTempToken != null) {
            visibleDialog = SettingsDialog.NONE
        }
    }

    LaunchedEffect(deleteTempToken) {
        if (deleteTempToken != null) {
            visibleDialog = SettingsDialog.NONE
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(3000)
            successMessage = null
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colorResource(R.color.background)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SettingsMenu(
                onThemeClick = { visibleDialog = SettingsDialog.THEME },
                onLanguageClick = { visibleDialog = SettingsDialog.LANGUAGE },
                onPasswordChangeClick = { visibleDialog = SettingsDialog.PASSWORD },
                onDeleteClick = { visibleDialog = SettingsDialog.DELETE_ACCOUNT },
            )

            successMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = Color.Green,
                    fontSize = 14.sp,
                    fontFamily = Montserrat,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }

        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.background).copy(alpha = 0.7f)),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(R.color.button_enabled),
                )
            }
        }

        when (visibleDialog) {
            SettingsDialog.THEME -> {
                ThemeChooserDialog(
                    currentTheme = theme,
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onThemeSelected = { selectedTheme ->
                        viewModel.setThemePreference(selectedTheme)
                        visibleDialog = SettingsDialog.NONE
                    },
                )
            }
            SettingsDialog.LANGUAGE -> {
                LanguageChooserDialog(
                    currentLanguage = language,
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onLanguageSelected = { selectedLanguage ->
                        viewModel.setLanguagePreference(selectedLanguage)
                        visibleDialog = SettingsDialog.NONE
                    },
                )
            }
            SettingsDialog.PASSWORD -> {
                PasswordChangeDialog(
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onSubmit = { currentPassword, newPassword ->
                        viewModel.initiatePasswordChange(
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                        ) { result ->
                            result.onSuccess {
                            }.onFailure { error ->
                                errorMessage = error.message
                            }
                        }
                    },
                    isLoading = isLoading,
                )
            }
            SettingsDialog.DELETE_ACCOUNT -> {
                DeleteAccountDialog(
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onConfirm = {
                        viewModel.initiateAccountDeletion { result ->
                            result.onSuccess {
                            }.onFailure { error ->
                                errorMessage = error.message
                            }
                        }
                    },
                    isLoading = isLoading,
                )
            }
            SettingsDialog.NONE -> { // do nothing
            }
        }

        if (passwordTempToken != null) {
            OtpDialog(
                email = user?.email?.let { maskEmail(it) } ?: "",
                isLoading = isLoading,
                error = passwordOtpError,
                onOtpSubmit = { otpCode ->
                    viewModel.verifyPasswordOtp(otpCode) { result ->
                        result.onSuccess { message ->
                            successMessage = message
                        }
                    }
                },
                onDismiss = {
                    viewModel.cancelPasswordOtp()
                },
            )
        }

        if (deleteTempToken != null) {
            OtpDialog(
                email = user?.email?.let { maskEmail(it) } ?: "",
                isLoading = isLoading,
                error = deleteOtpError,
                onOtpSubmit = { otpCode ->
                    viewModel.confirmAccountDeletion(otpCode) { result ->
                        result.onSuccess {
                            authStateViewModel.logout()
                        }
                    }
                },
                onDismiss = {
                    viewModel.cancelDeleteOtp()
                },
            )
        }

        errorMessage?.let { error ->
            ErrorDialog(
                errorMessage = error,
                onDismiss = { errorMessage = null },
            )
        }
    }
}
