package hse.diploma.cybersecplatform.data.model

data class LoginResponse(
    val token: String,
    val user: UserData,
)
