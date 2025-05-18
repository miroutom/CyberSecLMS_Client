package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.data.model.ForgotPasswordRequest
import hse.diploma.cybersecplatform.data.model.LoginRequest
import hse.diploma.cybersecplatform.data.model.LoginResponse
import hse.diploma.cybersecplatform.data.model.RegisterRequest
import hse.diploma.cybersecplatform.data.model.RegisterResponse
import hse.diploma.cybersecplatform.data.model.ResetPasswordRequest
import hse.diploma.cybersecplatform.data.model.SuccessResponse
import hse.diploma.cybersecplatform.data.model.TempTokenResponse
import hse.diploma.cybersecplatform.data.model.VerifyOtpRequest
import javax.inject.Inject

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
            val response = apiService.register(RegisterRequest(username, password, email, fullName))

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
            val response = apiService.verifyOtp(request)

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

    override suspend fun forgotPassword(email: String?, username: String?): Result<TempTokenResponse> {
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
        newPassword: String
    ): Result<SuccessResponse> {
        return try {
            val request = ResetPasswordRequest(
                tempToken = tempToken,
                code = code,
                newPassword = newPassword
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
        tempToken: String
    ): Result<SuccessResponse> {
        return try {
            val request = VerifyOtpRequest(
                otp = otpValue,
                tempToken = tempToken
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
