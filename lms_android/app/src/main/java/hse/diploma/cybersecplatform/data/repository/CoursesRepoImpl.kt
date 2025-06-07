package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.request.CreateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.CreateTaskRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateCourseRequest
import hse.diploma.cybersecplatform.data.model.request.UpdateTaskRequest
import hse.diploma.cybersecplatform.data.model.response.AllCoursesResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.MyCoursesResponse
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepoImpl @Inject constructor(
    private val apiService: ApiService,
) : CoursesRepo {
    override suspend fun getAllCourses(): Result<AllCoursesResponse> {
        return safeApiCall {
            apiService.getAllCourses().let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get all courses"))
                }
            }
        }
    }

    override suspend fun getMyCourses(): Result<MyCoursesResponse> {
        return safeApiCall {
            apiService.getMyCourses().let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get my courses"))
                }
            }
        }
    }

    override suspend fun getCourseById(courseId: Int): Result<Course> {
        return safeApiCall {
            apiService.getCourseById(courseId).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get course"))
                }
            }
        }
    }

    override suspend fun createCourse(request: CreateCourseRequest): Result<Course> {
        return safeApiCall {
            apiService.createCourse(request).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create course"))
                }
            }
        }
    }

    override suspend fun updateCourse(
        courseId: Int,
        request: UpdateCourseRequest,
    ): Result<Course> {
        return safeApiCall {
            apiService.updateCourse(courseId, request).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update course"))
                }
            }
        }
    }

    override suspend fun deleteCourse(courseId: Int): Result<MessageResponse> {
        return safeApiCall {
            apiService.deleteCourse(courseId).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to delete course"))
                }
            }
        }
    }

    override suspend fun createTask(
        courseId: Int,
        request: CreateTaskRequest,
    ): Result<Task> {
        return safeApiCall {
            apiService.createTask(courseId, request).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create task"))
                }
            }
        }
    }

    override suspend fun updateTask(
        courseId: Int,
        taskId: Int,
        request: UpdateTaskRequest,
    ): Result<Task> {
        return safeApiCall {
            apiService.updateTask(courseId, taskId, request).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update task"))
                }
            }
        }
    }

    override suspend fun deleteTask(
        courseId: Int,
        taskId: Int,
    ): Result<MessageResponse> {
        return safeApiCall {
            apiService.deleteTask(courseId, taskId).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Failed to delete task"))
                }
            }
        }
    }

    private inline fun <T> safeApiCall(block: () -> Result<T>): Result<T> {
        return try {
            block()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
