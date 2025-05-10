package hse.diploma.cybersecplatform.ui.state

import hse.diploma.cybersecplatform.domain.model.ErrorType

sealed class AllCoursesState {
    data object Loading : AllCoursesState()

    data class Success(val uiState: CoursesUiState) : AllCoursesState()

    data class Error(val errorType: ErrorType) : AllCoursesState()
}
