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
    open val login = _login.asStateFlow().value

    private val _password = MutableStateFlow(TextFieldValue(""))
    open val password = _password.asStateFlow().value

    private val _passwordConfirmation = MutableStateFlow(TextFieldValue(""))
    open val passwordConfirmation = _passwordConfirmation.asStateFlow().value

    private val _isRegistrationEnabled = MutableStateFlow(false)
    open val isRegistrationEnabled: Boolean = _isRegistrationEnabled.asStateFlow().value

    init {
        viewModelScope.launch {
            combine(
                _login,
                _password,
                _passwordConfirmation
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

    fun performRegistration() {
        // TODO: implement register logic
    }
}