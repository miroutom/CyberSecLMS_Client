package hse.diploma.cybersecplatform.domain.model

import com.google.gson.annotations.SerializedName
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType

data class Task(
    val id: Int,
    @SerializedName("course_id")
    val courseId: Int,
    val title: String,
    val description: String,
    val content: String,
    val solution: String,
    @SerializedName("vulnerability_type")
    val vulnerabilityType: VulnerabilityType,
    val number: Int,
    val difficulty: Difficulty,
    val type: String,
    val points: Int,
    @SerializedName("is_completed")
    var isCompleted: Boolean = false,
    @SerializedName("language")
    val language: String = "javascript",
) {
    fun checkSolution(userCode: String): Boolean {
        return userCode.trim() == solution.trim()
    }
}
