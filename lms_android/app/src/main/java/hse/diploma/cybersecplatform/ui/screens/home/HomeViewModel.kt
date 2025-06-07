package hse.diploma.cybersecplatform.ui.screens.home

import androidx.lifecycle.viewModelScope
import dagger.Module
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.CoursesSharedViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Module
class HomeViewModel @Inject constructor(
    private val coursesRepo: CoursesRepo,
) : CoursesSharedViewModel() {
    init {
        loadCourses()
    }

    override fun loadCourses() {
        viewModelScope.launch {
            _coursesState.value = CoursesState.Loading
            try {
                val result = coursesRepo.getAllCourses()
                result.onSuccess { response ->
                    _coursesState.value =
                        CoursesState.Success(
                            courses = response.courses,
                            filteredCourses = response.courses,
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
                            course.description.contains(query, ignoreCase = true) ||
                            course.vulnerabilityType.toString().contains(query, ignoreCase = true)
                    }
                }
            _coursesState.value = currentState.copy(filteredCourses = filtered)
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
