package hse.diploma.cybersecplatform.data.model.response

import hse.diploma.cybersecplatform.domain.model.Course

data class AllCoursesResponse(
    val courses: List<Course>,
)
