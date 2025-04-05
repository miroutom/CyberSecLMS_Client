package hse.diploma.cybersecplatform.ui.screens.auth


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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.TextButton
import hse.diploma.cybersecplatform.ui.components.textFields.AuthMethodTextField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordConfirmationField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordField
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.ui.theme.linearHorizontalGradient
import hse.diploma.cybersecplatform.utils.isLoginValidAndAuthMethodType
import hse.diploma.cybersecplatform.utils.isPasswordValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RegistrationScreen(
    onNavigateToAuthorization: () -> Unit,
    viewModel: RegistrationScreenViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val login by viewModel.login.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordConfirmation by viewModel.passwordConfirmation.collectAsState()
    val isRegistrationEnabled by viewModel.isRegistrationEnabled.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(130.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = Typography.titleLarge.copy(
                    brush = linearHorizontalGradient
                )
            )
            Text(
                text = stringResource(R.string.registration_title),
                color = colorResource(R.color.supporting_text),
                style = Typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(84.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthMethodTextField(
                    value = login,
                    onValueChange = viewModel::onLoginChange,
                    modifier = Modifier.fillMaxWidth()
                )
                PasswordField(
                    value = password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier.fillMaxWidth()
                )
                PasswordConfirmationField(
                    value = passwordConfirmation,
                    passwordValue = password,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(64.dp))
            FilledButton(
                text = stringResource(R.string.register_button),
                onClick = viewModel::performRegistration,
                enabled = isRegistrationEnabled
            )
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(
                text = stringResource(R.string.have_account_button),
                onClick = onNavigateToAuthorization
            )
        }
    }
}

@Preview
@Composable
fun RegistrationScreenPreview() {
    val mockViewModel = object : RegistrationScreenViewModel() {
        override val login: StateFlow<TextFieldValue>
            get() = MutableStateFlow(TextFieldValue("9300315295"))
        override val password: StateFlow<TextFieldValue>
            get() = MutableStateFlow(TextFieldValue("8991A.64783k1"))
        override val passwordConfirmation: StateFlow<TextFieldValue>
            get() = MutableStateFlow(TextFieldValue("8991A.64k783k1"))
        override val isRegistrationEnabled: StateFlow<Boolean>
            get() = MutableStateFlow(
                isLoginValidAndAuthMethodType(login.value.text).first &&
                        isPasswordValid(password.value.text) &&
                        password.value.text == passwordConfirmation.value.text
            )
    }
    RegistrationScreen(
        onNavigateToAuthorization = {},
        viewModel = mockViewModel
    )
}
