package hse.diploma.cybersecplatform.ui.screens.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.data.model.user.UserStats
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.shared.AccountDeletionState
import hse.diploma.cybersecplatform.ui.state.shared.PasswordState
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepo,
    private val authRepository: AuthRepo,
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _passwordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val passwordState: StateFlow<PasswordState> = _passwordState.asStateFlow()

    private val _accountDeletionState = MutableStateFlow<AccountDeletionState>(AccountDeletionState.Idle)
    val accountDeletionState: StateFlow<AccountDeletionState> = _accountDeletionState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val result = userRepository.getUserProfile()

                result.onSuccess { user ->
                    _profileState.value =
                        ProfileState.Success(
                            ProfileUiState(
                                userData = user,
                                stats =
                                    UserStats(
                                        totalCourses = user.courses.size,
                                        completedTasks = user.completedTasks,
                                        totalTasks = user.totalTasks,
                                        progress = user.progress,
                                    ),
                            ),
                        )
                }.onFailure { e ->
                    _profileState.value =
                        ProfileState.Error(
                            e.toErrorType(TAG) ?: ErrorType.Other,
                        )
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun updateProfile(
        fullName: String,
        email: String,
    ) {
        viewModelScope.launch {
            try {
                val currentState = (_profileState.value as? ProfileState.Success)?.uiState
                if (currentState == null) {
                    _profileState.value = ProfileState.Error(ErrorType.Other)
                    return@launch
                }

                _profileState.value = ProfileState.Loading

                val updatedUser =
                    currentState.userData.copy(
                        fullName = fullName,
                        email = email,
                    )

                val result = userRepository.updateProfile(updatedUser)
                result.onSuccess {
                    loadProfile()
                }.onFailure { e ->
                    _profileState.value = ProfileState.Error(e.toErrorType(TAG) ?: ErrorType.Other)
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun uploadAvatar(
        avatarUri: Uri,
        contentResolver: ContentResolver,
    ) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val result = userRepository.uploadAvatar(avatarUri, contentResolver)
                result.onSuccess { user ->
                    _profileState.value =
                        ProfileState.Success(
                            ProfileUiState(
                                userData = user,
                                stats =
                                    UserStats(
                                        totalCourses = user.courses.size,
                                        completedTasks = user.completedTasks,
                                        totalTasks = user.totalTasks,
                                        progress = user.progress,
                                    ),
                            ),
                        )
                }.onFailure { e ->
                    _profileState.value = ProfileState.Error(e.toErrorType(TAG) ?: ErrorType.Other)
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
    ) {
        viewModelScope.launch {
            _passwordState.value = PasswordState.Loading
            try {
                val result = authRepository.changePassword(currentPassword, newPassword)
                result.onSuccess {
                    _passwordState.value = PasswordState.Success
                }.onFailure { e ->
                    _passwordState.value = PasswordState.Error(e.toErrorType(TAG) ?: ErrorType.Other)
                }
            } catch (e: Exception) {
                _passwordState.value = PasswordState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun requestAccountDeletion(password: String) {
        viewModelScope.launch {
            _accountDeletionState.value = AccountDeletionState.Loading
            try {
                val result = authRepository.requestDeleteAccount(password)
                result.onSuccess {
                    _accountDeletionState.value = AccountDeletionState.ConfirmationRequired
                }.onFailure { e ->
                    _accountDeletionState.value = AccountDeletionState.Error(e.toErrorType(TAG) ?: ErrorType.Other)
                }
            } catch (e: Exception) {
                _accountDeletionState.value = AccountDeletionState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun confirmAccountDeletion(code: String) {
        viewModelScope.launch {
            _accountDeletionState.value = AccountDeletionState.Loading
            try {
                val result = authRepository.confirmDeleteAccount(code)
                result.onSuccess {
                    _accountDeletionState.value = AccountDeletionState.Success
                    authRepository.logout()
                }.onFailure { e ->
                    _accountDeletionState.value = AccountDeletionState.Error(e.toErrorType(TAG) ?: ErrorType.Other)
                }
            } catch (e: Exception) {
                _accountDeletionState.value = AccountDeletionState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}

data class ProfileUiState(
    val userData: UserData,
    val stats: UserStats,
)
