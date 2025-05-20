package hse.diploma.cybersecplatform.ui.state

import hse.diploma.cybersecplatform.domain.model.ErrorType
import hse.diploma.cybersecplatform.ui.screens.settings.UpdatePasswordUiState

sealed class UpdatePasswordState {
    data object Loading : UpdatePasswordState()

    data class Success(val uiState: UpdatePasswordUiState) : UpdatePasswordState()

    data class Error(val errorType: ErrorType) : UpdatePasswordState()
}
