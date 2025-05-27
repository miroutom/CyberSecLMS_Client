package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.lifecycle.ViewModel
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthStateViewModel @Inject constructor(private val authRepo: AuthRepo) : ViewModel() {
    private val _isAuthorized = MutableStateFlow(authRepo.isAuthorized())
    val isAuthorized = _isAuthorized.asStateFlow()

    fun authorize() {
        _isAuthorized.value = true
    }

    fun logout() {
        authRepo.logout()
        _isAuthorized.value = false
    }
}
