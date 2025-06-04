package hse.diploma.cybersecplatform.ui.screens.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.UserData
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.logE
import hse.diploma.cybersecplatform.utils.retry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepo,
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            val result = retry { userRepository.getUserProfile() }

            if (result.isSuccess) {
                val user = result.getOrNull()!!
                _profileState.value =
                    ProfileState.Success(
                        ProfileUiState(userData = user),
                    )
            } else {
                _profileState.value =
                    ProfileState.Error(
                        result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other,
                    )
            }
        }
    }

    fun updateProfile(
        username: String,
        fullName: String,
        email: String,
    ) {
        logD(TAG, "updateProfile username: $username, fullName: $fullName, email: $email")
        val currentState = (profileState.value as? ProfileState.Success)?.uiState
        if (currentState == null) {
            _profileState.value = ProfileState.Error(ErrorType.Other)
            return
        }

        val currentImage = currentState.userData.profileImage

        viewModelScope.launch {
            try {
                val userData =
                    UserData(
                        username = username,
                        fullName = fullName,
                        email = email,
                        profileImage = currentImage,
                    )

                _profileState.value = ProfileState.Loading

                val result = userRepository.updateProfile(userData)
                if (result.isSuccess) {
                    val loadResult = userRepository.getUserProfile()
                    if (loadResult.isSuccess) {
                        val user = loadResult.getOrNull()!!
                        _profileState.value = ProfileState.Success(ProfileUiState(userData = user))
                    } else {
                        val errorType = loadResult.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other
                        _profileState.value = ProfileState.Error(errorType)
                    }
                } else {
                    val errorType = result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other
                    logD(TAG, "Update profile error: $errorType")
                    _profileState.value = ProfileState.Error(errorType)
                }
            } catch (e: Exception) {
                logE(TAG, "Exception while updating profile", e)
                _profileState.value = ProfileState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun uploadPhoto(
        avatarUri: Uri?,
        contentResolver: ContentResolver,
    ) {
        if (avatarUri == null) return
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val result = userRepository.uploadAvatar(avatarUri, contentResolver)
            if (result.isSuccess) {
                loadProfile()
                val user = result.getOrNull()!!
                _profileState.value =
                    ProfileState.Success(
                        ProfileUiState(
                            userData = user,
                        ),
                    )
            } else {
                _profileState.value =
                    ProfileState.Error(
                        result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other,
                    )
            }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}

data class ProfileUiState(
    val userData: UserData,
)
