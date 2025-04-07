package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.utils.isLoginValidAndAuthMethodType
import hse.diploma.cybersecplatform.utils.isPasswordValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

open class RegistrationScreenViewModel : ViewModel() {
    private val _login = MutableStateFlow(TextFieldValue(""))
    open val login = _login.asStateFlow()

    private val _password = MutableStateFlow(TextFieldValue(""))
    open val password = _password.asStateFlow()

    private val _passwordConfirmation = MutableStateFlow(TextFieldValue(""))
    open val passwordConfirmation = _passwordConfirmation.asStateFlow()

    private val _isRegistrationEnabled = MutableStateFlow(false)
    open val isRegistrationEnabled = _isRegistrationEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _login,
                _password,
                _passwordConfirmation,
            ) { login, password, passwordConfirmation ->
                isLoginValidAndAuthMethodType(login.text).first &&
                    isPasswordValid(password.text) &&
                    password.text == passwordConfirmation.text
            }.collect { isValid ->
                _isRegistrationEnabled.value = isValid
            }
        }
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

    fun performRegistration(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // TODO: connect to backend through REST API
            val isRegistered =
                login.value.text == "example@example.com" &&
                    password.value.text == "test123." &&
                    password.value.text == passwordConfirmation.value.text

            if (isRegistered) {
                onSuccess()
            } else {
                // TODO: implement error state
            }
        }
    }
}
