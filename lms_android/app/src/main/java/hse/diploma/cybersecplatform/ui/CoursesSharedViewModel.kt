package hse.diploma.cybersecplatform.ui

import androidx.lifecycle.ViewModel
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class CoursesSharedViewModel : ViewModel() {
    protected val _coursesState = MutableStateFlow<CoursesState>(CoursesState.Loading)
    val coursesState: StateFlow<CoursesState> = _coursesState.asStateFlow()

    protected val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterCourses(query)
    }

    abstract fun loadCourses()

    protected abstract fun filterCourses(query: String)

    sealed class CoursesState {
        data object Loading : CoursesState()

        data class Success(
            val courses: List<Course>,
            val filteredCourses: List<Course> = emptyList(),
        ) : CoursesState()

        data class Error(val error: ErrorType) : CoursesState()
    }
}
