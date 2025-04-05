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

open class AuthorizationScreenViewModel : ViewModel() {

    private val _login = MutableStateFlow(TextFieldValue(""))
    open val login = _login.asStateFlow()

    private val _password = MutableStateFlow(TextFieldValue(""))
    open val password = _password.asStateFlow()

    private val _isAuthorizationEnabled = MutableStateFlow(false)
    open val isAuthorizationEnabled = _isAuthorizationEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_login, _password) { login, password ->
                isLoginValidAndAuthMethodType(login.text).first && isPasswordValid(password.text)
            }.collect { isValid ->
                _isAuthorizationEnabled.value = isValid
            }
        }
    }

    fun onLoginChange(newLogin: TextFieldValue) {
        _login.value = newLogin
    }

    fun onPasswordChange(newPassword: TextFieldValue) {
        _password.value = newPassword
    }

    fun performAuthorization() {
        // TODO: implement auth logic
    }
}
