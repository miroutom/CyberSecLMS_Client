package hse.diploma.cybersecplatform.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.ProfileState
import hse.diploma.cybersecplatform.data.model.ProfileUiState
import hse.diploma.cybersecplatform.domain.repository.UserRepo
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
                _profileState.value = ProfileState.Error(result.exceptionOrNull()?.message ?: "Profile loading error")
            }
        }
    }
}
