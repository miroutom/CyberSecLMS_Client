package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.ui.components.dialogs.DeleteAccountDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.ErrorDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.LanguageChooserDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.OtpDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.PasswordChangeDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.ThemeChooserDialog
import hse.diploma.cybersecplatform.ui.components.menu.SettingsDialog
import hse.diploma.cybersecplatform.ui.components.menu.SettingsMenu
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen
import hse.diploma.cybersecplatform.ui.state.screen_state.SettingsScreenState
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.maskEmail
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    state: SettingsScreenState,
    onThemeSelected: (AppTheme) -> Unit,
    onLanguageSelected: (Language) -> Unit,
    onPasswordChangeInitiated: (String, String, (Result<String>) -> Unit) -> Unit,
    onDeleteAccountInitiated: (String, (Result<String>) -> Unit) -> Unit,
    onPasswordOtpSubmitted: (String, (Result<String>) -> Unit) -> Unit,
    onDeleteOtpSubmitted: (String, (Result<String>) -> Unit) -> Unit,
    onPasswordOtpDismissed: () -> Unit,
    onDeleteOtpDismissed: () -> Unit,
    onErrorDismissed: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visibleDialog by remember { mutableStateOf(SettingsDialog.NONE) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            delay(3000)
            onErrorDismissed()
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

            state.successMessage?.let {
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

        if (state.isLoading) {
            LoadingScreen()
        }

        when (visibleDialog) {
            SettingsDialog.THEME ->
                ThemeChooserDialog(
                    currentTheme = state.theme,
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onThemeSelected = {
                        onThemeSelected(it)
                        visibleDialog = SettingsDialog.NONE
                    },
                )

            SettingsDialog.LANGUAGE ->
                LanguageChooserDialog(
                    currentLanguage = state.language,
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onLanguageSelected = {
                        onLanguageSelected(it)
                        visibleDialog = SettingsDialog.NONE
                    },
                )

            SettingsDialog.PASSWORD ->
                PasswordChangeDialog(
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onSubmit = { currentPassword, newPassword ->
                        onPasswordChangeInitiated(currentPassword, newPassword) { result ->
                            result.onFailure { error ->
                                // handled in viewModel
                            }
                        }
                    },
                    isLoading = state.isLoading,
                )

            SettingsDialog.DELETE_ACCOUNT ->
                DeleteAccountDialog(
                    onDismiss = { visibleDialog = SettingsDialog.NONE },
                    onConfirm = { password ->
                        onDeleteAccountInitiated(password) { result ->
                            result.onFailure { error ->
                                // handled in viewModel
                            }
                        }
                    },
                    isLoading = state.isLoading,
                )

            SettingsDialog.NONE -> {}
        }

        state.passwordTempToken?.let {
            OtpDialog(
                email = state.user?.email?.let { maskEmail(it) } ?: "",
                isLoading = state.isLoading,
                error = state.passwordOtpError,
                onOtpSubmit = { otpCode ->
                    onPasswordOtpSubmitted(otpCode) { result ->
                        result.onSuccess { message ->
                            // handled in viewModel
                        }
                    }
                },
                onDismiss = onPasswordOtpDismissed,
            )
        }

        state.deleteTempToken?.let {
            OtpDialog(
                email = state.user?.email?.let { maskEmail(it) } ?: "",
                isLoading = state.isLoading,
                error = state.deleteOtpError,
                onOtpSubmit = { otpCode ->
                    onDeleteOtpSubmitted(otpCode) { result ->
                        result.onSuccess {
                            onLogout()
                        }
                    }
                },
                onDismiss = onDeleteOtpDismissed,
            )
        }

        state.errorMessage?.let { error ->
            ErrorDialog(
                errorMessage = error,
                onDismiss = onErrorDismissed,
            )
        }
    }
}

@Composable
@Preview(name = "SettingsScreen", showBackground = true, apiLevel = 30)
private fun SettingsScreenPreview() {
    CyberSecPlatformTheme {
        SettingsScreen(
            state = SettingsScreenState(),
            onThemeSelected = {},
            onLanguageSelected = {},
            onPasswordChangeInitiated = { _, _, _ -> },
            onDeleteAccountInitiated = { _, _ -> },
            onPasswordOtpSubmitted = { _, _ -> },
            onDeleteOtpSubmitted = { _, _ -> },
            onPasswordOtpDismissed = {},
            onDeleteOtpDismissed = {},
            onErrorDismissed = {},
            onLogout = {},
        )
    }
}
