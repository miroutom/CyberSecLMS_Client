package hse.diploma.cybersecplatform.ui.state.shared

import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.model.Course

sealed class TeacherCoursesState {
    data object Loading : TeacherCoursesState()

    data class Success(val courses: List<Course>) : TeacherCoursesState()

    data class Error(val error: ErrorType) : TeacherCoursesState()
}
