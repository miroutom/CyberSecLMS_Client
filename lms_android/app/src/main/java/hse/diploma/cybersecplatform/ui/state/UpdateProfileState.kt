package hse.diploma.cybersecplatform.ui.state

import hse.diploma.cybersecplatform.domain.model.ErrorType
import hse.diploma.cybersecplatform.ui.screens.settings.UpdateProfileUiState

sealed class UpdateProfileState {
    data object Loading : UpdateProfileState()

    data class Success(val uiState: UpdateProfileUiState) : UpdateProfileState()

    data class Error(val errorType: ErrorType) : UpdateProfileState()
}
