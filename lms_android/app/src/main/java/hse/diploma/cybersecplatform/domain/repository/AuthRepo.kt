package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.model.response.LoginResponse
import hse.diploma.cybersecplatform.data.model.response.MessageResponse
import hse.diploma.cybersecplatform.data.model.response.RegisterResponse
import hse.diploma.cybersecplatform.data.model.response.TempTokenResponse

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
        isTeacher: Boolean,
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

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): Result<Map<String, String>>

    suspend fun requestDeleteAccount(password: String): Result<TempTokenResponse>

    suspend fun confirmDeleteAccount(code: String): Result<MessageResponse>

    fun logout()

    fun isAuthorized(): Boolean
}
