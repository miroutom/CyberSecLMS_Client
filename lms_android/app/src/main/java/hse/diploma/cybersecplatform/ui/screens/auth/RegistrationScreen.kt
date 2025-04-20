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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.MainApplication
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.TextButton
import hse.diploma.cybersecplatform.ui.components.textFields.AdditionalTextField
import hse.diploma.cybersecplatform.ui.components.textFields.AuthMethodTextField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordConfirmationField
import hse.diploma.cybersecplatform.ui.components.textFields.PasswordField
import hse.diploma.cybersecplatform.ui.theme.Typography
import hse.diploma.cybersecplatform.ui.theme.linearHorizontalGradient
import hse.diploma.cybersecplatform.utils.logD

private const val TAG = "RegistrationScreen"

@Composable
fun RegistrationScreen(
    onNavigateToAuthorization: () -> Unit,
    onRegistered: () -> Unit,
    onError: (String) -> Unit,
    viewModel: RegistrationScreenViewModel = viewModel(factory = LocalViewModelFactory.current),
    modifier: Modifier = Modifier,
) {
    val login by viewModel.login.collectAsState()
    val password by viewModel.password.collectAsState()
    val username by viewModel.username.collectAsState()
    val fullName by viewModel.fullName.collectAsState()

    val passwordConfirmation by viewModel.passwordConfirmation.collectAsState()
    val isRegistrationEnabled by viewModel.isRegistrationEnabled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
                if (isLoading) {
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
                    Spacer(modifier = Modifier.height(130.dp))
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
                        AdditionalTextField(
                            value = fullName,
                            onValueChange = viewModel::onFullNameChange,
                            labelId = R.string.auth_label_full_name,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AdditionalTextField(
                            value = username,
                            onValueChange = viewModel::onUsernameChange,
                            labelId = R.string.auth_label_username,
                            modifier = Modifier.fillMaxWidth(),
                        )
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
                        PasswordConfirmationField(
                            value = passwordConfirmation,
                            passwordValue = password,
                            onValueChange = viewModel::onConfirmPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    FilledButton(
                        text = stringResource(R.string.register_button),
                        onClick = {
                            logD(TAG, "onRegisterClicked register")
                            viewModel.register(
                                username = username.text,
                                password = password.text,
                                email = login.text,
                                fullName = fullName.text,
                            ) { result ->
                                result.onSuccess {
                                    onRegistered()
                                }.onFailure { error ->
                                    onError(error.message ?: "Ошибка регистрации")
                                }
                            }
                        },
                        enabled = isRegistrationEnabled,
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

@Preview
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(
        onNavigateToAuthorization = {},
        onRegistered = {},
        onError = {},
        viewModel = MainApplication.appComponent.viewModelFactory().create(RegistrationScreenViewModel::class.java),
    )
}
