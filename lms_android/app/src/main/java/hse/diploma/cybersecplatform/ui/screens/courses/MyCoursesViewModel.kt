package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.shared.MyCoursesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyCoursesViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val coursesRepo: CoursesRepo,
) : ViewModel() {
    private val _myCoursesState = MutableStateFlow<MyCoursesState>(MyCoursesState.Loading)
    val myCoursesState: StateFlow<MyCoursesState> = _myCoursesState.asStateFlow()

    private val _selectedCourseForRestart = MutableStateFlow<Course?>(null)
    val selectedCourseForRestart: StateFlow<Course?> = _selectedCourseForRestart.asStateFlow()

    init {
        loadCourses()
    }

    fun selectCourseForRestart(course: Course) {
        _selectedCourseForRestart.value = course
    }

    fun loadCourses() {
        viewModelScope.launch {
            _myCoursesState.value = MyCoursesState.Loading

            val profileResult = userRepo.getUserProfile()
            if (profileResult.isFailure) {
                _myCoursesState.value =
                    MyCoursesState.Error(
                        profileResult.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other,
                    )
                return@launch
            }

            val userId = profileResult.getOrNull()!!.id

            val coursesResult = coursesRepo.getAllCourses()
            if (coursesResult.isFailure) {
                _myCoursesState.value =
                    MyCoursesState.Error(
                        coursesResult.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other,
                    )
                return@launch
            }

            val statsResult = userRepo.getUserStatistics(userId)
            if (statsResult.isFailure) {
                _myCoursesState.value =
                    MyCoursesState.Error(
                        statsResult.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other,
                    )
                return@launch
            }

            val allCourses = coursesResult.getOrNull()!!
            val userStatistics = statsResult.getOrNull()!!

            val enrichedCourses =
                allCourses.map { course ->
                    val progress = userStatistics.coursesProgress.find { it.courseId == course.id }
                    if (progress != null) {
                        course.copy(
                            completedTasks = (progress.completionPercentage * course.tasksCount / 100).toInt(),
                            isStarted = progress.completionPercentage > 0,
                            progress = progress.completionPercentage.toInt(),
                        )
                    } else {
                        course
                    }
                }

            _myCoursesState.value =
                MyCoursesState.Success(
                    CoursesUiState(
                        courses = enrichedCourses,
                        startedCourses =
                            enrichedCourses.filter {
                                it.progress < 100
                            },
                        completedCourses = enrichedCourses.filter { it.isCompleted() },
                    ),
                )
        }
    }

    fun onCompletedCourseRestart(course: Course) {
        val currentState = _myCoursesState.value
        if (currentState is MyCoursesState.Success) {
            val updatedCourse =
                course.copy(
                    completedTasks = 0,
                    progress = 0,
                    isStarted = true,
                )

            val updatedCourses =
                currentState.uiState.courses.map {
                    if (it.id == course.id) updatedCourse else it
                }

            val newUiState =
                currentState.uiState.copy(
                    courses = updatedCourses,
                    startedCourses =
                        updatedCourses.filter {
                            it.progress < 100
                        },
                    completedCourses = updatedCourses.filter { it.isCompleted() },
                )

            _myCoursesState.value = MyCoursesState.Success(newUiState)
        }
    }

    companion object {
        private const val TAG = "MyCoursesViewModel"
    }
}

data class CoursesUiState(
    val courses: List<Course> = emptyList(),
    val startedCourses: List<Course> = emptyList(),
    val completedCourses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList(),
)
