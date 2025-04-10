package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.TextButton
import hse.diploma.cybersecplatform.ui.components.textFields.AuthMethodTextField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordField
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.ui.theme.linearHorizontalGradient
import hse.diploma.cybersecplatform.utils.isLoginValidAndAuthMethodType
import hse.diploma.cybersecplatform.utils.isPasswordValid
import hse.diploma.cybersecplatform.utils.logD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "AuthorizationScreen"

@Composable
fun AuthorizationScreen(
    onNavigateToRegistration: () -> Unit,
    onAuthorized: () -> Unit,
    viewModel: AuthorizationScreenViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val login by viewModel.login.collectAsState()
    val password by viewModel.password.collectAsState()
    val isAuthorizationEnabled by viewModel.isAuthorizationEnabled.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(paddingValues),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
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
                        AuthMethodTextField(
                            value = login,
                            onValueChange = viewModel::onLoginChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        PasswordField(
                            value = password,
                            onValueChange = viewModel::onPasswordChange,
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
                        onClick = {
                            logD(TAG, "onLoginClicked performAuthorization")
                            viewModel.performAuthorization(onAuthorized)
                        },
                        enabled = isAuthorizationEnabled,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    TextButton(
                        text = stringResource(R.string.no_account_button),
                        onClick = onNavigateToRegistration,
                    )
                }
            }
        },
    )
}

@Preview
@Composable
fun AuthorizationScreenPreview() {
    val mockViewModel =
        object : AuthorizationScreenViewModel() {
            override val login: StateFlow<TextFieldValue>
                get() = MutableStateFlow(TextFieldValue("9300315295"))
            override val password: StateFlow<TextFieldValue>
                get() = MutableStateFlow(TextFieldValue("8991A.64783k1"))
            override val isAuthorizationEnabled: StateFlow<Boolean>
                get() =
                    MutableStateFlow(
                        isLoginValidAndAuthMethodType(login.value.text).first &&
                            isPasswordValid(password.value.text),
                    )
        }

    AuthorizationScreen(
        onNavigateToRegistration = {},
        onAuthorized = {},
        viewModel = mockViewModel,
    )
}
