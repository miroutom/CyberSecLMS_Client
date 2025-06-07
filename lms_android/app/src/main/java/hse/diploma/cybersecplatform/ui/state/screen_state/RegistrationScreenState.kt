package hse.diploma.cybersecplatform.ui.state.screen_state

import androidx.compose.ui.text.input.TextFieldValue

data class RegistrationScreenState(
    val login: TextFieldValue = TextFieldValue(),
    val password: TextFieldValue = TextFieldValue(),
    val username: TextFieldValue = TextFieldValue(),
    val fullName: TextFieldValue = TextFieldValue(),
    val passwordConfirmation: TextFieldValue = TextFieldValue(),
    val isRegistrationEnabled: Boolean = false,
    val isTeacher: Boolean = false,
    val isLoading: Boolean = false,
)
