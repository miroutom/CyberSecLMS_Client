package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.response.AllCoursesResponse
import hse.diploma.cybersecplatform.data.model.response.MyCoursesResponse
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.mock.mockAllCourses
import hse.diploma.cybersecplatform.mock.mockCourses
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepoImpl @Inject constructor(
    private val apiService: ApiService,
) : CoursesRepo {
    override suspend fun getAllCourses(): Result<List<Course>> {
        return try {
            val response = Response.success(AllCoursesResponse(mockAllCourses))

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
            val response = Response.success(MyCoursesResponse(mockCourses))

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
