package hse.diploma.cybersecplatform.data.model.request

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val fullName: String,
)
