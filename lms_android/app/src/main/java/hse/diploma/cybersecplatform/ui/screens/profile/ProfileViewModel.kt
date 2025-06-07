package hse.diploma.cybersecplatform.ui.screens.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.data.model.analytics.UserStatistics
import hse.diploma.cybersecplatform.data.model.user.UserData
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.shared.ProfileState
import hse.diploma.cybersecplatform.utils.logE
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
            try {
                val userProfileResult = userRepository.getUserProfile()

                userProfileResult.onSuccess { user ->
                    val statsResult = userRepository.getUserStatistics(user.id)

                    statsResult.onSuccess { stats ->
                        _profileState.value =
                            ProfileState.Success(
                                ProfileUiState(
                                    userData = user,
                                    stats = stats,
                                ),
                            )
                    }.onFailure { e ->
                        val courses = user.courses ?: emptyList()

                        val courseProgressList =
                            courses.map { course ->
                                UserStatistics.CourseProgress(
                                    averageScore = 0.0,
                                    completionPercentage = course.progress,
                                    courseId = course.courseId,
                                    courseName = course.title ?: "N/A",
                                    lastActivity = user.lastLogin ?: "N/A",
                                )
                            }

                        val basicStats =
                            UserStatistics(
                                averageScore = 0.0,
                                completedCourses = courses.count { it.progress >= 100.0 },
                                completedTasks = user.completedTasks,
                                coursesProgress = courseProgressList,
                                joinedDate = "N/A",
                                lastActive = user.lastLogin ?: "N/A",
                                totalCourses = courses.size,
                                totalPoints = 0,
                                totalTasks = user.totalTasks,
                                userId = user.id,
                            )

                        _profileState.value =
                            ProfileState.Success(
                                ProfileUiState(
                                    userData = user,
                                    stats = basicStats,
                                ),
                            )

                        logE(TAG, "Failed to load statistics", e)
                    }
                }.onFailure { e ->
                    _profileState.value = ProfileState.Error(e.toErrorType(TAG))
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.toErrorType(TAG))
            }
        }
    }

    fun updateProfile(
        username: String,
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
                    _profileState.value = ProfileState.Error(e.toErrorType(TAG))
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
                    val currentState = (_profileState.value as? ProfileState.Success)?.uiState
                    val currentStats = currentState?.stats

                    if (currentStats != null) {
                        _profileState.value =
                            ProfileState.Success(
                                ProfileUiState(
                                    userData = user,
                                    stats = currentStats,
                                ),
                            )
                    } else {
                        val courses = user.courses ?: emptyList()

                        val courseProgressList =
                            courses.map { course ->
                                UserStatistics.CourseProgress(
                                    averageScore = 0.0,
                                    completionPercentage = course.progress,
                                    courseId = course.courseId,
                                    courseName = course.title ?: "N/A",
                                    lastActivity = user.lastLogin ?: "N/A",
                                )
                            }

                        val basicStats =
                            UserStatistics(
                                averageScore = 0.0,
                                completedCourses = courses.count { it.progress >= 100.0 },
                                completedTasks = user.completedTasks,
                                coursesProgress = courseProgressList,
                                joinedDate = "N/A",
                                lastActive = user.lastLogin ?: "N/A",
                                totalCourses = courses.size,
                                totalPoints = 0,
                                totalTasks = user.totalTasks,
                                userId = user.id,
                            )

                        _profileState.value =
                            ProfileState.Success(
                                ProfileUiState(
                                    userData = user,
                                    stats = basicStats,
                                ),
                            )
                    }
                }.onFailure { e ->
                    _profileState.value = ProfileState.Error(e.toErrorType(TAG))
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.toErrorType(TAG))
            }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}

data class ProfileUiState(
    val userData: UserData,
    val stats: UserStatistics,
)
