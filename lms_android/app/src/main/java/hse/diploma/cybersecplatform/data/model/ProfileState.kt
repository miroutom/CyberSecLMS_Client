package hse.diploma.cybersecplatform.data.model

sealed class ProfileState {
    data object Loading : ProfileState()

    data class Success(val profile: ProfileUiState) : ProfileState()

    data class Error(val message: String) : ProfileState()
}
