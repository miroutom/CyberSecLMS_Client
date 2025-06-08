package hse.diploma.cybersecplatform.data.model.analytics

import com.google.gson.annotations.SerializedName

data class UserStatistics(
    @SerializedName("average_score")
    val averageScore: Double,
    @SerializedName("completed_courses")
    val completedCourses: Int,
    @SerializedName("completed_tasks")
    val completedTasks: Int,
    @SerializedName("courses_progress")
    val coursesProgress: List<CourseProgress>,
    @SerializedName("joined_date")
    val joinedDate: String,
    @SerializedName("last_active")
    val lastActive: String,
    @SerializedName("total_courses")
    val totalCourses: Int,
    @SerializedName("total_points")
    val totalPoints: Int,
    @SerializedName("total_tasks")
    val totalTasks: Int,
    @SerializedName("user_id")
    val userId: Int,
) {
    data class CourseProgress(
        val averageScore: Double,
        val completionPercentage: Double,
        val courseId: Int,
        val courseName: String,
        val lastActivity: String,
    )
}
