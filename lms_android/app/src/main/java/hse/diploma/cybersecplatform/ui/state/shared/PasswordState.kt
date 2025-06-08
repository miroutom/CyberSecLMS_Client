package hse.diploma.cybersecplatform.ui.state.shared

import hse.diploma.cybersecplatform.domain.error.ErrorType

sealed class PasswordState {
    data object Idle : PasswordState()

    data object Loading : PasswordState()

    data object Success : PasswordState()

    data class Error(val error: ErrorType) : PasswordState()
}
