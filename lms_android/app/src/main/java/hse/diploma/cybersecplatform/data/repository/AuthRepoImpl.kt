package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.data.model.request.ForgotPasswordRequest
import hse.diploma.cybersecplatform.data.model.request.LoginRequest
import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.request.RegisterRequest
import hse.diploma.cybersecplatform.data.model.response.RegisterResponse
import hse.diploma.cybersecplatform.data.model.request.ResetPasswordRequest
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse
import hse.diploma.cybersecplatform.data.model.request.VerifyOtpRequest
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepoImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
) : AuthRepo {
    override suspend fun login(
        username: String,
        password: String,
    ): Result<TempTokenResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                response.body()?.let { tempTokenResponse ->
                    Result.success(tempTokenResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
            }
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
            val response = apiService.register(RegisterRequest(username, password, email, fullName))

            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    // Сохраняем токен для автоматической авторизации
                    tokenManager.saveToken(registerResponse.token)
                    Result.success(registerResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
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
            val response = apiService.verifyOtp(request)

            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    tokenManager.saveToken(loginResponse.token)
                    Result.success(loginResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "OTP verification failed"))
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

    override suspend fun forgotPassword(
        email: String?,
        username: String?,
    ): Result<TempTokenResponse> {
        return try {
            val request = ForgotPasswordRequest(email = email, username = username)
            val response = apiService.forgotPassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to reset password"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(
        tempToken: String,
        code: String,
        newPassword: String,
    ): Result<MessageResponse> {
        return try {
            val request =
                ResetPasswordRequest(
                    tempToken = tempToken,
                    code = code,
                    newPassword = newPassword,
                )
            val response = apiService.resetPassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to reset password"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestDeleteAccount(): Result<TempTokenResponse> {
        return try {
            val response = apiService.requestDeleteAccount()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to request account deletion"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmDeleteAccount(
        otpValue: String,
        tempToken: String,
    ): Result<MessageResponse> {
        return try {
            val request =
                VerifyOtpRequest(
                    otp = otpValue,
                    tempToken = tempToken,
                )
            val response = apiService.confirmDeleteAccount(request)

            if (response.isSuccessful && response.body() != null) {
                logout()
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to delete account"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
