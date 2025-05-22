package hse.diploma.cybersecplatform.ui.state

import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.ui.screens.courses.CoursesUiState

sealed class MyCoursesState {
    data object Loading : MyCoursesState()

    data class Success(val uiState: CoursesUiState) : MyCoursesState()

    data class Error(val errorType: ErrorType) : MyCoursesState()
}
