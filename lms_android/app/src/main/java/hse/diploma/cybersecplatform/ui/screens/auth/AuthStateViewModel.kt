package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthStateViewModel : ViewModel() {
    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized = _isAuthorized.asStateFlow()

    fun authorize() {
        _isAuthorized.value = true
    }

    fun logout() {
        _isAuthorized.value = false
    }
}
