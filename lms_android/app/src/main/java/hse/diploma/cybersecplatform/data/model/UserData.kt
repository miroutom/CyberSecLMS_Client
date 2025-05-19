package hse.diploma.cybersecplatform.data.model

import com.google.gson.annotations.SerializedName

data class UserData(
    val username: String,
    val fullName: String,
    val email: String,
    @SerializedName("profileImage")
    val profileImage: String?,
)
