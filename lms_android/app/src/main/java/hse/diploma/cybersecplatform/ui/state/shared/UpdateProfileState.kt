package hse.diploma.cybersecplatform.ui.state.shared

import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileUiState

sealed class UpdateProfileState {
    data object Loading : UpdateProfileState()

    data class Success(val uiState: ProfileUiState) : UpdateProfileState()

    data class Error(val errorType: ErrorType) : UpdateProfileState()
}
