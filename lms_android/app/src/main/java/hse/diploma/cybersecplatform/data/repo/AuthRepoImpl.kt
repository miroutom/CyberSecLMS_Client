package hse.diploma.cybersecplatform.data.repo

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.data.model.LoginRequest
import hse.diploma.cybersecplatform.data.model.LoginResponse
import hse.diploma.cybersecplatform.data.model.RegisterRequest
import hse.diploma.cybersecplatform.domain.AuthRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepoImpl @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager,
) : AuthRepo {
    override suspend fun login(
        username: String,
        password: String,
    ): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            response.body()?.token?.let { tokenManager.saveToken(it) }
            Result.success(response.body()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        email: String,
        fullName: String,
    ): Result<Unit> {
        return try {
            val response = api.register(RegisterRequest(username, password, email, fullName))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        tokenManager.clearToken()
    }

    override fun isAuthorized(): Boolean {
        return tokenManager.hasToken()
    }
}
