package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import hse.diploma.cybersecplatform.WithTheme
import hse.diploma.cybersecplatform.ui.state.screen_state.RegistrationScreenState

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun PreviewRegistrationScreen() =
    WithTheme {
        val state =
            RegistrationScreenState(
                fullName = TextFieldValue("John Doe"),
                username = TextFieldValue("johndoe"),
                login = TextFieldValue("test@example.com"),
                password = TextFieldValue("password123"),
                passwordConfirmation = TextFieldValue("password123"),
                isRegistrationEnabled = true,
            )

        RegistrationScreen(
            state = state,
            onFullNameChange = {},
            onUsernameChange = {},
            onLoginChange = {},
            onTeacherStatusChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRegisterClick = {},
            onNavigateToAuthorization = {},
        )
    }
