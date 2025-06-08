package hse.diploma.cybersecplatform.data.model.user

import com.google.gson.annotations.SerializedName

data class LearningPath(
    @SerializedName("generated_at")
    val generatedAt: String,
    @SerializedName("next_tasks")
    val nextTasks: List<NextTask>,
    val recommendations: List<CourseRecommendation>,
    val skills: List<SkillProgress>,
    @SerializedName("user_id")
    val userId: Int,
) {
    data class NextTask(
        val courseId: Int,
        val courseName: String,
        val dueDate: String?,
        val priority: Int,
        val taskId: Int,
        val taskTitle: String,
    )

    data class CourseRecommendation(
        val courseId: Int,
        val courseName: String,
        val estimatedTime: String,
        val priority: Int,
        val reason: String,
    )

    data class SkillProgress(
        val currentLevel: Int,
        val progressToNextLevel: Double,
        val recommendedTaskId: Int?,
        val skillName: String,
    )
}
