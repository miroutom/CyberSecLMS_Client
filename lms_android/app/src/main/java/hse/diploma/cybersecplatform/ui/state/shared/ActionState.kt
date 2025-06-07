package hse.diploma.cybersecplatform.ui.state.shared

import hse.diploma.cybersecplatform.domain.error.ErrorType

sealed class ActionState {
    data object Idle : ActionState()

    data object Loading : ActionState()

    data class Success(val message: String) : ActionState()

    data class Error(val error: ErrorType) : ActionState()
}
