package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepoImpl @Inject constructor(
    private val apiService: ApiService,
) : CoursesRepo {
    override suspend fun getAllCourses(): Result<List<Course>> {
        return try {
            val response = apiService.getAllCourses()

            if (response.isSuccessful) {
                val message = response.body()?.courses
                if (message != null) {
                    Result.success(message)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get all courses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyCourses(): Result<List<Course>> {
        return try {
            val response = apiService.getMyCourses()

            if (response.isSuccessful) {
                val message = response.body()?.courses
                if (message != null) {
                    Result.success(message)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get your courses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
