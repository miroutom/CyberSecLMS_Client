package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.domain.model.ErrorType
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileScreenViewModel @Inject constructor(
    private val userRepository: UserRepo,
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val result = userRepository.getUserProfile()
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                _profileState.value =
                    ProfileState.Success(
                        ProfileUiState(
                            fullName = user.fullName,
                            email = user.email,
                        ),
                    )
            } else {
                _profileState.value = ProfileState.Error(result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other)
            }
        }
    }

    companion object {
        private const val TAG = "ProfileScreenViewModel"
    }
}

data class ProfileUiState(
    val fullName: String,
    val email: String,
)
