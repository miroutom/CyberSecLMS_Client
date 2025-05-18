package hse.diploma.cybersecplatform.data.model

data class UserData(
    val username: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String? = null,
)
