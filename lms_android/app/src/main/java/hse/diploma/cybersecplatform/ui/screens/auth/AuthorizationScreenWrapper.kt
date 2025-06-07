package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.screens.otp.OtpViewModel
import hse.diploma.cybersecplatform.ui.state.screen_state.AuthorizationScreenState

@Composable
fun AuthorizationScreenWrapper(
    viewModel: AuthorizationViewModel = viewModel(factory = LocalViewModelFactory.current),
    otpViewModel: OtpViewModel = viewModel(factory = LocalViewModelFactory.current),
    onNavigateToRegistration: () -> Unit,
    onAuthorized: () -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state =
        AuthorizationScreenState(
            username = viewModel.username.collectAsState().value,
            password = viewModel.password.collectAsState().value,
            isAuthorizationEnabled = viewModel.isAuthorizationEnabled.collectAsState().value,
            isLoading = viewModel.isLoading.collectAsState().value,
        )

    var tempTokenForOtp by remember { mutableStateOf<String?>(null) }
    var otpError by remember { mutableStateOf<String?>(null) }
    val isOtpLoading by otpViewModel.isLoading.collectAsState()
    var errorDialogMessage by remember { mutableStateOf<String?>(null) }

    AuthorizationScreen(
        state =
            state.copy(
                tempTokenForOtp = tempTokenForOtp,
                otpError = otpError,
                errorDialogMessage = errorDialogMessage,
            ),
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = {
            viewModel.login { result ->
                result.onSuccess { tempResp ->
                    tempTokenForOtp = tempResp.tempToken
                    otpError = null
                }.onFailure { error ->
                    errorDialogMessage = error.message ?: "Authorization error"
                }
            }
        },
        onNavigateToRegistration = onNavigateToRegistration,
        onOtpSubmit = { enteredCode ->
            tempTokenForOtp?.let { token ->
                otpViewModel.verifyOtp(
                    tempToken = token,
                    otp = enteredCode,
                    onResult = { result ->
                        result.onSuccess {
                            tempTokenForOtp = null
                            otpError = null
                            onAuthorized()
                        }.onFailure { error ->
                            otpError = error.message ?: "OTP verification failed"
                            errorDialogMessage = error.message ?: "OTP verification failed"
                        }
                    },
                )
            }
        },
        onOtpDismiss = {
            tempTokenForOtp = null
            otpError = null
        },
        onErrorDismiss = { errorDialogMessage = null },
        isOtpLoading = isOtpLoading,
        modifier = modifier,
    )
}
