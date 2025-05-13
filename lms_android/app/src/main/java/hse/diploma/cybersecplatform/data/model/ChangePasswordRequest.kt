package hse.diploma.cybersecplatform.data.model

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
