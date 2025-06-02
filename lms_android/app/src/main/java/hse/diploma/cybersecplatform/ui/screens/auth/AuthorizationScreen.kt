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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.TextButton
import hse.diploma.cybersecplatform.ui.components.dialogs.ErrorDialog
import hse.diploma.cybersecplatform.ui.components.dialogs.OtpDialog
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordField
import hse.diploma.cybersecplatform.ui.components.textFields.RegistrationTextField
import hse.diploma.cybersecplatform.ui.screens.loading.LoadingScreen
import hse.diploma.cybersecplatform.ui.state.screen_state.AuthorizationScreenState
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.ui.theme.linearHorizontalGradient

private const val TAG = "AuthorizationScreen"

@Composable
fun AuthorizationScreen(
    state: AuthorizationScreenState,
    onUsernameChange: (TextFieldValue) -> Unit,
    onPasswordChange: (TextFieldValue) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onOtpSubmit: (String) -> Unit,
    onOtpDismiss: () -> Unit,
    onErrorDismiss: () -> Unit,
    isOtpLoading: Boolean = false,
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
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                ) {
                    Spacer(modifier = Modifier.height(130.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style =
                            Typography.titleLarge.copy(
                                brush = linearHorizontalGradient,
                            ),
                    )
                    Text(
                        text = stringResource(R.string.authorization_title),
                        color = colorResource(R.color.supporting_text),
                        style = Typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(84.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RegistrationTextField(
                            value = state.username,
                            onValueChange = onUsernameChange,
                            labelId = R.string.auth_label_username,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        PasswordField(
                            value = state.password,
                            onValueChange = onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        TextButton(
                            text = stringResource(R.string.forgot_password_button),
                            onClick = {},
                            textAlign = TextAlign.End,
                        )
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                    FilledButton(
                        text = stringResource(R.string.auth_button),
                        onClick = onLoginClick,
                        enabled = state.isAuthorizationEnabled,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    TextButton(
                        text = stringResource(R.string.no_account_button),
                        onClick = onNavigateToRegistration,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.tempTokenForOtp != null) {
                        OtpDialog(
                            email = "",
                            isLoading = isOtpLoading,
                            error = state.otpError,
                            onOtpSubmit = onOtpSubmit,
                            onDismiss = onOtpDismiss,
                        )
                    }
                }

                if (state.isLoading) {
                    LoadingScreen()
                }

                state.errorDialogMessage?.let { msg ->
                    ErrorDialog(
                        errorMessage = msg,
                        onDismiss = onErrorDismiss,
                    )
                }
            }
        },
    )
}

@Composable
@PreviewLightDark
@Preview(name = "AuthorizationScreen", showSystemUi = true, showBackground = true)
fun AuthorizationScreenPreview() {
    val previewState =
        AuthorizationScreenState(
            username = TextFieldValue("test@example.com"),
            password = TextFieldValue("password123"),
            isAuthorizationEnabled = true,
        )

    AuthorizationScreen(
        state = previewState,
        onUsernameChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onNavigateToRegistration = {},
        onOtpSubmit = {},
        onOtpDismiss = {},
        onErrorDismiss = {},
    )
}
