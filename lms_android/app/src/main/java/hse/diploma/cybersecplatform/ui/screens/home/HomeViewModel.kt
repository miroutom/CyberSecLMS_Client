package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Module
import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.extensions.toErrorType
import hse.diploma.cybersecplatform.ui.screens.courses.CoursesUiState
import hse.diploma.cybersecplatform.ui.state.shared.AllCoursesState
import hse.diploma.cybersecplatform.utils.logD
import hse.diploma.cybersecplatform.utils.toVulnerabilityType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Module
class HomeViewModel @Inject constructor(
    private val coursesRepo: CoursesRepo,
) : ViewModel() {
    private val _allCoursesState = MutableStateFlow<AllCoursesState>(AllCoursesState.Loading)
    val allCoursesState: StateFlow<AllCoursesState> = _allCoursesState.asStateFlow()

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        logD(TAG, "loadCourses")
        viewModelScope.launch {
            _allCoursesState.value = AllCoursesState.Loading
            val result = coursesRepo.getAllCourses()
            logD(TAG, "loadCourses result = $result")
            if (result.isSuccess) {
                val courses = result.getOrNull()!!
                _allCoursesState.value =
                    AllCoursesState.Success(
                        CoursesUiState(
                            courses = courses,
                            startedCourses = courses.filter { it.isStarted },
                            completedCourses = courses.filter { !it.isStarted },
                        ),
                    )
            } else {
                _allCoursesState.value =
                    AllCoursesState.Error(result.exceptionOrNull()?.toErrorType(TAG) ?: ErrorType.Other)
            }
        }
    }

    fun onSearchQueryChange(newSearchQuery: TextFieldValue) {
        logD(TAG, "onSearchQueryChange query = $newSearchQuery")
        _searchQuery.value = newSearchQuery
        val currentState = _allCoursesState.value
        if (currentState is AllCoursesState.Success) {
            val query = newSearchQuery.text.trim().lowercase()
            val filtered =
                if (query.isBlank()) {
                    currentState.uiState.courses
                } else {
                    currentState.uiState.courses.filter {
                        it.vulnerabilityType.toVulnerabilityType().name.lowercase().contains(query)
                    }
                }
            _allCoursesState.value =
                currentState.copy(
                    uiState = currentState.uiState.copy(filteredCourses = filtered),
                )
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
