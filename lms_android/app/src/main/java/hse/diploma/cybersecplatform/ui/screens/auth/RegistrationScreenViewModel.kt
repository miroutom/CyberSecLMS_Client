package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.RegisterResponse
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.utils.isLoginValidAndAuthMethodType
import hse.diploma.cybersecplatform.utils.isPasswordValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationScreenViewModel @Inject constructor(private val authRepo: AuthRepo) : ViewModel() {
    private val _fullName = MutableStateFlow(TextFieldValue(""))
    val fullName = _fullName.asStateFlow()

    private val _username = MutableStateFlow(TextFieldValue(""))
    val username = _username.asStateFlow()

    private val _login = MutableStateFlow(TextFieldValue(""))
    val login = _login.asStateFlow()

    private val _password = MutableStateFlow(TextFieldValue(""))
    val password = _password.asStateFlow()

    private val _passwordConfirmation = MutableStateFlow(TextFieldValue(""))
    val passwordConfirmation = _passwordConfirmation.asStateFlow()

    private val _isRegistrationEnabled = MutableStateFlow(false)
    val isRegistrationEnabled = _isRegistrationEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _login,
                _password,
                _passwordConfirmation,
            ) { login, password, passwordConfirmation ->
                isLoginValidAndAuthMethodType(login.text) &&
                    isPasswordValid(password.text) &&
                    password.text == passwordConfirmation.text
            }.collect { isValid ->
                _isRegistrationEnabled.value = isValid
            }
        }
    }

    fun onFullNameChange(newFullName: TextFieldValue) {
        _fullName.value = newFullName
    }

    fun onUsernameChange(newUsername: TextFieldValue) {
        _username.value = newUsername
    }

    fun onLoginChange(newLogin: TextFieldValue) {
        _login.value = newLogin
    }

    fun onPasswordChange(newPassword: TextFieldValue) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newPasswordConfirmation: TextFieldValue) {
        _passwordConfirmation.value = newPasswordConfirmation
    }

    fun register(
        username: String,
        password: String,
        email: String,
        fullName: String,
        onResult: (Result<RegisterResponse>) -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                onResult(authRepo.register(username, password, email, fullName))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
