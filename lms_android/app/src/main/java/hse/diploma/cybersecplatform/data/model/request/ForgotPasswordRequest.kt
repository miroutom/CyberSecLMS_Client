package hse.diploma.cybersecplatform.data.model.request

data class ForgotPasswordRequest(
    val email: String? = null,
    val username: String? = null,
)
