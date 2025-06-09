package hse.diploma.cybersecplatform.domain.model

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id")
    val id: Int,
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("solution")
    val solution: String,
    @SerializedName("vulnerabilityType")
    val vulnerabilityType: String,
    @SerializedName("order")
    val number: Int,
    @SerializedName("difficulty")
    val difficulty: String,
    val type: String,
    @SerializedName("points")
    val points: Int,
    @SerializedName("isCompleted")
    var isCompleted: Boolean = false,
    val language: String = "javascript",
) {
    fun checkSolution(userCode: String): Boolean {
        return userCode.trim() == solution.trim()
    }
}
