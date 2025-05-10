package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.domain.model.Course
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
                val courses = response.body()
                if (courses != null) {
                    Result.success(courses)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load courses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyCourses(): Result<List<Course>> {
        return try {
            val response = apiService.getMyCourses()
            if (response.isSuccessful) {
                val courses = response.body()
                if (courses != null) {
                    Result.success(courses)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load courses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
