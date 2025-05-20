package hse.diploma.cybersecplatform.data.model

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
