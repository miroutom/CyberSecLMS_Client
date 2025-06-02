package hse.diploma.cybersecplatform.ui.state.screen_state

import hse.diploma.cybersecplatform.domain.error.ErrorType
import hse.diploma.cybersecplatform.domain.model.Course

data class MyCoursesScreenState(
    val isStartedSelected: Boolean = true,
    val showResetDialog: Boolean = false,
    val courseToRestart: Course? = null,
    val isLoading: Boolean = false,
    val error: ErrorType? = null,
    val startedCourses: List<Course> = emptyList(),
    val completedCourses: List<Course> = emptyList(),
)
