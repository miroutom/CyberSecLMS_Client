package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.TextButton
import hse.diploma.cybersecplatform.ui.components.textFields.AuthorizationTextField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordConfirmationField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordField
import hse.diploma.cybersecplatform.ui.components.textFields.RegistrationTextField
import hse.diploma.cybersecplatform.ui.state.screen_state.RegistrationScreenState
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.ui.theme.linearHorizontalGradient

private const val TAG = "RegistrationScreen"

@Composable
fun RegistrationScreen(
    state: RegistrationScreenState,
    onFullNameChange: (TextFieldValue) -> Unit,
    onUsernameChange: (TextFieldValue) -> Unit,
    onLoginChange: (TextFieldValue) -> Unit,
    onPasswordChange: (TextFieldValue) -> Unit,
    onConfirmPasswordChange: (TextFieldValue) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToAuthorization: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.background))
                        .padding(paddingValues),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                ) {
                    Spacer(modifier = Modifier.height(100.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style =
                            Typography.titleLarge.copy(
                                brush = linearHorizontalGradient,
                            ),
                    )
                    Text(
                        text = stringResource(R.string.registration_title),
                        color = colorResource(R.color.supporting_text),
                        style = Typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(42.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RegistrationTextField(
                            value = state.fullName,
                            onValueChange = onFullNameChange,
                            labelId = R.string.auth_label_full_name,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        RegistrationTextField(
                            value = state.username,
                            onValueChange = onUsernameChange,
                            labelId = R.string.auth_label_username,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AuthorizationTextField(
                            value = state.login,
                            onValueChange = onLoginChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        PasswordField(
                            value = state.password,
                            onValueChange = onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        PasswordConfirmationField(
                            value = state.passwordConfirmation,
                            passwordValue = state.password,
                            onValueChange = onConfirmPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    FilledButton(
                        text = stringResource(R.string.register_button),
                        onClick = onRegisterClick,
                        enabled = state.isRegistrationEnabled,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    TextButton(
                        text = stringResource(R.string.have_account_button),
                        onClick = onNavigateToAuthorization,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
    )
}

@Composable
@Preview(name = "RegistrationScreen", showBackground = true, apiLevel = 30)
fun RegistrationScreenPreview() {
    val previewState =
        RegistrationScreenState(
            login = TextFieldValue("test@example.com"),
            password = TextFieldValue("password123"),
            username = TextFieldValue("testuser"),
            fullName = TextFieldValue("Test User"),
            passwordConfirmation = TextFieldValue("password123"),
            isRegistrationEnabled = true,
        )

    RegistrationScreen(
        state = previewState,
        onFullNameChange = {},
        onUsernameChange = {},
        onLoginChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onRegisterClick = {},
        onNavigateToAuthorization = {},
    )
}
