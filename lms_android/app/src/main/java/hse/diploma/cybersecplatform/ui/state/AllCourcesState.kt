package hse.diploma.cybersecplatform.ui.state

import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.ui.screens.courses.CoursesUiState

sealed class AllCoursesState {
    data object Loading : AllCoursesState()

    data class Success(val uiState: CoursesUiState) : AllCoursesState()

    data class Error(val errorType: ErrorType) : AllCoursesState()
}
