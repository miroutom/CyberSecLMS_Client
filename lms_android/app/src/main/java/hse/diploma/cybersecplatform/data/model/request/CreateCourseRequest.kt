package hse.diploma.cybersecplatform.data.model.request

import com.google.gson.annotations.SerializedName

data class CreateCourseRequest(
    val title: String,
    val description: String,
    @SerializedName("difficulty_level")
    val difficultyLevel: String,
    val category: String,
)
