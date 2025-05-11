package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.ErrorType
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.state.CoursesUiState
import hse.diploma.cybersecplatform.ui.state.MyCoursesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyCoursesScreenViewModel @Inject constructor(
    private val coursesRepo: CoursesRepo,
) : ViewModel() {
    private val _myCoursesState = MutableStateFlow<MyCoursesState>(MyCoursesState.Loading)
    val myCoursesState: StateFlow<MyCoursesState> = _myCoursesState.asStateFlow()

    fun loadCourses() {
        viewModelScope.launch {
            _myCoursesState.value = MyCoursesState.Loading
            val result = coursesRepo.getMyCourses()
            if (result.isSuccess) {
                val courses = result.getOrNull()!!
                _myCoursesState.value =
                    MyCoursesState.Success(
                        CoursesUiState(
                            courses = courses,
                            startedCourses = courses.filter { it.isStarted },
                            completedCourses = courses.filter { !it.isStarted },
                        ),
                    )
            } else {
                _myCoursesState.value =
                    MyCoursesState.Error(result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other)
            }
        }
    }

    fun onCompletedCourseRestart(course: Course) {
        val currentState = _myCoursesState.value
        if (currentState is MyCoursesState.Success) {
            val updatedCourse = course.copy(completedTasks = 0, isStarted = true)
            val updatedCourses =
                currentState.uiState.courses.map {
                    if (it == course) updatedCourse else it
                }
            val newUiState =
                currentState.uiState.copy(
                    courses = updatedCourses,
                    startedCourses = updatedCourses.filter { it.isStarted },
                    completedCourses = updatedCourses.filter { !it.isStarted },
                )
            _myCoursesState.value = MyCoursesState.Success(newUiState)
        }
    }

    companion object {
        private const val TAG = "MyCoursesScreenViewModel"
    }
}
