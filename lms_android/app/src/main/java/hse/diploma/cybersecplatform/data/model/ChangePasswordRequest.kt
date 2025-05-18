package hse.diploma.cybersecplatform.data.model

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
