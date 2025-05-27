package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.domain.model.Course

interface CoursesRepo {
    suspend fun getMyCourses(): Result<List<Course>>

    suspend fun getAllCourses(): Result<List<Course>>
}
