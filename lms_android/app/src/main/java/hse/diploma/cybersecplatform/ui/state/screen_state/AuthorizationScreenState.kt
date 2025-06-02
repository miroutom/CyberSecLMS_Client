package hse.diploma.cybersecplatform.ui.state.screen_state

import androidx.compose.ui.text.input.TextFieldValue

data class AuthorizationScreenState(
    val username: TextFieldValue = TextFieldValue(),
    val password: TextFieldValue = TextFieldValue(),
    val isAuthorizationEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val tempTokenForOtp: String? = null,
    val otpError: String? = null,
    val errorDialogMessage: String? = null,
)
