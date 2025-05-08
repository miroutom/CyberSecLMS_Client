package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.model.UserData

interface UserRepo {
    suspend fun getUserProfile(): Result<UserData>
}
