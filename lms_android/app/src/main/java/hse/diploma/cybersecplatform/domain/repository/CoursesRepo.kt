package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.model.request.CreateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.CreateTaskRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateTaskRequest
import hse.diploma.cybersecplatform.data.model.response.AllCoursesResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.MyCoursesResponse
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task

interface CoursesRepo {
    suspend fun getMyCourses(): Result<MyCoursesResponse>

    suspend fun getAllCourses(): Result<AllCoursesResponse>

    suspend fun getCourseById(courseId: Int): Result<Course>

    suspend fun createCourse(request: CreateCourseRequest): Result<Course>

    suspend fun updateCourse(
        courseId: Int,
        request: UpdateCourseRequest,
    ): Result<Course>

    suspend fun deleteCourse(courseId: Int): Result<MessageResponse>

    suspend fun createTask(
        courseId: Int,
        request: CreateTaskRequest,
    ): Result<Task>

    suspend fun updateTask(
        courseId: Int,
        taskId: Int,
        request: UpdateTaskRequest,
    ): Result<Task>

    suspend fun deleteTask(
        courseId: Int,
        taskId: Int,
    ): Result<MessageResponse>
}
