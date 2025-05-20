package hse.diploma.cybersecplatform.data.model

data class ResetPasswordRequest(
    val tempToken: String,
    val code: String,
    val newPassword: String,
)
