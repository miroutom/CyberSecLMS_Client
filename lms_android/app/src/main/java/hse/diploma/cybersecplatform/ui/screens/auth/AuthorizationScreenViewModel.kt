package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.TempTokenResponse
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.utils.isPasswordValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthorizationScreenViewModel @Inject constructor(private val authRepo: AuthRepo) : ViewModel() {
    private val _username = MutableStateFlow(TextFieldValue(""))
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow(TextFieldValue(""))
    val password = _password.asStateFlow()

    private val _isAuthorizationEnabled = MutableStateFlow(false)
    val isAuthorizationEnabled = _isAuthorizationEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_password, _username) { password, username ->
                username.text.isNotEmpty() && isPasswordValid(password.text)
            }.collect { isValid ->
                _isAuthorizationEnabled.value = isValid
            }
        }
    }

    fun onUsernameChange(newUsername: TextFieldValue) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: TextFieldValue) {
        _password.value = newPassword
    }

    fun login(
        username: String,
        password: String,
        onResult: (Result<TempTokenResponse>) -> Unit,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                onResult(authRepo.login(username, password))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
