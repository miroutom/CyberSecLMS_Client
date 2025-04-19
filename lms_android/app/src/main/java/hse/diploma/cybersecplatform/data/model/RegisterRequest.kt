package hse.diploma.cybersecplatform.data.model

data class RegisterRequest(
    val username: String,
    val password: String,
    // TODO: add phone register possibility
    val email: String,
    // TODO: divide to name and surname
    val fullName: String,
)
