package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.model.LoginResponse
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

    fun logout()

    fun isAuthorized(): Boolean
}
