package hse.diploma.cybersecplatform.data.model.request

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
