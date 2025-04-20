package hse.diploma.cybersecplatform.domain

import hse.diploma.cybersecplatform.data.model.LoginResponse

interface AuthRepo {
    suspend fun login(
        username: String,
        password: String,
    ): Result<LoginResponse>

    suspend fun register(
        username: String,
        password: String,
        email: String,
        fullName: String,
    ): Result<Unit>

    fun logout()

    fun isAuthorized(): Boolean
}
