package hse.diploma.cybersecplatform.ui.state.shared

import hse.diploma.cybersecplatform.domain.error.ErrorType

sealed class AccountDeletionState {
    data object Idle : AccountDeletionState()

    data object Loading : AccountDeletionState()

    data object ConfirmationRequired : AccountDeletionState()

    data object Success : AccountDeletionState()

    data class Error(val error: ErrorType) : AccountDeletionState()
}
