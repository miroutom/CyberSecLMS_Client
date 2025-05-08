package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.UserData
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val apiService: ApiService,
) : UserRepo {
    override suspend fun getUserProfile(): Result<UserData> {
        return try {
            val response = apiService.getUserProfile()
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
