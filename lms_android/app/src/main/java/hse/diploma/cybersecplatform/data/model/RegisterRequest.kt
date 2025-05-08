package hse.diploma.cybersecplatform.data.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val fullName: String,
)
