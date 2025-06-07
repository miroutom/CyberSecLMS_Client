package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.lifecycle.viewModelScope
import dagger.Module
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.CoursesSharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Module
class MyCoursesViewModel @Inject constructor(
    private val coursesRepo: CoursesRepo,
) : CoursesSharedViewModel() {
    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    init {
        loadCourses()
    }

    override fun loadCourses() {
        viewModelScope.launch {
            _coursesState.value = CoursesState.Loading
            try {
                val result = coursesRepo.getMyCourses()
                result.onSuccess { response ->
                    val courses = response.courses
                    _coursesState.value =
                        CoursesState.Success(
                            courses = courses,
                            filteredCourses = courses,
                        )
                }.onFailure { e ->
                    _coursesState.value = CoursesState.Error(e.toErrorType(TAG))
                }
            } catch (e: Exception) {
                _coursesState.value = CoursesState.Error(e.toErrorType(TAG))
            }
        }
    }

    override fun filterCourses(query: String) {
        val currentState = _coursesState.value
        if (currentState is CoursesState.Success) {
            val filtered =
                if (query.isBlank()) {
                    currentState.courses
                } else {
                    currentState.courses.filter { course ->
                        course.title.contains(query, ignoreCase = true) ||
                            course.description.contains(query, ignoreCase = true)
                    }
                }
            _coursesState.value = currentState.copy(filteredCourses = filtered)
        }
    }

    fun selectCourse(course: Course) {
        _selectedCourse.value = course
    }

    fun restartCourseProgress(course: Course) {
        val currentState = _coursesState.value
        if (currentState is CoursesState.Success) {
            val updatedCourses =
                currentState.courses.map {
                    if (it.id == course.id) it.copy(completedTasks = 0, progress = 0) else it
                }
            _coursesState.value =
                currentState.copy(
                    courses = updatedCourses,
                    filteredCourses =
                        updatedCourses.filter {
                            _searchQuery.value.isBlank() || it.title.contains(_searchQuery.value, ignoreCase = true)
                        },
                )
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
