package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.data.model.LoginRequest
import hse.diploma.cybersecplatform.data.model.LoginResponse
import hse.diploma.cybersecplatform.data.model.RegisterRequest
import hse.diploma.cybersecplatform.data.model.RegisterResponse
import hse.diploma.cybersecplatform.data.model.TempTokenResponse
import hse.diploma.cybersecplatform.data.model.VerifyOtpRequest
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager,
) : AuthRepo {
    override suspend fun login(
        username: String,
        password: String,
    ): Result<TempTokenResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            response.body()?.let { tempTokenResponse ->
                Result.success(tempTokenResponse)
            } ?: Result.failure(Exception("No body"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        email: String,
        fullName: String,
    ): Result<RegisterResponse> {
        return try {
            val response = api.register(RegisterRequest(username, password, email, fullName))
            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    tokenManager.saveToken(registerResponse.token)
                    Result.success(registerResponse)
                } ?: Result.failure(Exception("No body"))
            } else {
                Result.failure(Exception("OTP verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(
        otpValue: String,
        tempToken: String,
    ): Result<LoginResponse> {
        return try {
            val request = VerifyOtpRequest(otpValue, tempToken)
            val response = api.verifyOtp(request)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    tokenManager.saveToken(loginResponse.token)
                    Result.success(loginResponse)
                } ?: Result.failure(Exception("No body"))
            } else {
                Result.failure(Exception("OTP verification failed"))
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
