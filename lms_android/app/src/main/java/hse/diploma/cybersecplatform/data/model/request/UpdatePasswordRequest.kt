package hse.diploma.cybersecplatform.data.model.request

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
