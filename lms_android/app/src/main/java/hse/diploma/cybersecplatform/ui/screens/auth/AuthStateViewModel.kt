package hse.diploma.cybersecplatform.ui.screens.auth

import androidx.lifecycle.ViewModel
import dagger.Module
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Module
class AuthStateViewModel
    @Inject
    constructor() : ViewModel() {
        private val _isAuthorized = MutableStateFlow(false)
        val isAuthorized = _isAuthorized.asStateFlow()

        fun authorize() {
            _isAuthorized.value = true
        }

        fun logout() {
            _isAuthorized.value = false
        }
    }
