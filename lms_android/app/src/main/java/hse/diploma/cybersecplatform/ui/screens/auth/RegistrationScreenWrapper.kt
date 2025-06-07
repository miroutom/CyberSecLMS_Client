package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.state.screen_state.RegistrationScreenState

@Composable
fun RegistrationScreenWrapper(
    viewModel: RegistrationViewModel = viewModel(factory = LocalViewModelFactory.current),
    onNavigateToAuthorization: () -> Unit,
    onRegistered: () -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state =
        RegistrationScreenState(
            login = viewModel.login.collectAsState().value,
            password = viewModel.password.collectAsState().value,
            username = viewModel.username.collectAsState().value,
            fullName = viewModel.fullName.collectAsState().value,
            isTeacher = viewModel.isTeacher.collectAsState().value,
            passwordConfirmation = viewModel.passwordConfirmation.collectAsState().value,
            isRegistrationEnabled = viewModel.isRegistrationEnabled.collectAsState().value,
            isLoading = viewModel.isLoading.collectAsState().value,
        )

    RegistrationScreen(
        state = state,
        onFullNameChange = viewModel::onFullNameChange,
        onUsernameChange = viewModel::onUsernameChange,
        onLoginChange = viewModel::onLoginChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTeacherStatusChange = viewModel::onTeacherStatusChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegisterClick = {
            viewModel.register(
                username = state.username.text,
                password = state.password.text,
                email = state.login.text,
                fullName = state.fullName.text,
            ) { result ->
                result.onSuccess {
                    onRegistered()
                }.onFailure { error ->
                    onError(error.message ?: "Ошибка регистрации")
                }
            }
        },
        onNavigateToAuthorization = onNavigateToAuthorization,
        modifier = modifier,
    )
}
