package hse.diploma.cybersecplatform.ui.state

import hse.diploma.cybersecplatform.domain.model.Course

data class CoursesUiState(
    val courses: List<Course> = emptyList(),
    val startedCourses: List<Course> = emptyList(),
    val completedCourses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList(),
)
