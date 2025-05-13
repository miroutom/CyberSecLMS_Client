package hse.diploma.cybersecplatform.ui.state

import hse.diploma.cybersecplatform.domain.model.ErrorType

sealed class UpdateProfileState {
    data object Idle : UpdateProfileState()
    data object Loading : UpdateProfileState()
    data object Success : UpdateProfileState()
    data class Error(val errorType: ErrorType?) : UpdateProfileState()
}

