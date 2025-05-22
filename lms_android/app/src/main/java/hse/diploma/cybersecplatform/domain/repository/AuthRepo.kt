package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.model.LoginResponse
import hse.diploma.cybersecplatform.data.model.MessageResponse
import hse.diploma.cybersecplatform.data.model.RegisterResponse
import hse.diploma.cybersecplatform.data.model.TempTokenResponse

interface AuthRepo {
    suspend fun login(
        username: String,
        password: String,
    ): Result<TempTokenResponse>

    suspend fun register(
        username: String,
        password: String,
        email: String,
        fullName: String,
    ): Result<RegisterResponse>

    suspend fun verifyOtp(
        otpValue: String,
        tempToken: String,
    ): Result<LoginResponse>

    suspend fun forgotPassword(
        email: String? = null,
        username: String? = null,
    ): Result<TempTokenResponse>

    suspend fun resetPassword(
        tempToken: String,
        code: String,
        newPassword: String,
    ): Result<MessageResponse>

    suspend fun requestDeleteAccount(): Result<TempTokenResponse>

    suspend fun confirmDeleteAccount(
        otpValue: String,
        tempToken: String,
    ): Result<MessageResponse>

    fun logout()

    fun isAuthorized(): Boolean
}
