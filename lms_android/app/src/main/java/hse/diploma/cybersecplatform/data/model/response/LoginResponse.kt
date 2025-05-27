package hse.diploma.cybersecplatform.data.model.response

import hse.diploma.cybersecplatform.data.model.UserData

data class LoginResponse(
    val token: String,
    val user: UserData,
)
