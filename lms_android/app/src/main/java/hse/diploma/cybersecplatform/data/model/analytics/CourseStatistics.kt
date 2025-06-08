package hse.diploma.cybersecplatform.data.model.analytics

import com.google.gson.annotations.SerializedName

data class CourseStatistics(
    @SerializedName("average_completion_percentage")
    val averageCompletionPercentage: Double,
    @SerializedName("average_score")
    val averageScore: Double,
    @SerializedName("completed_students")
    val completedStudents: Int,
    @SerializedName("course_id")
    val courseId: Int,
    @SerializedName("course_name")
    val courseName: String,
    @SerializedName("enrolled_students")
    val enrolledStudents: Int,
    @SerializedName("students_progress")
    val studentsProgress: List<StudentProgress>,
    @SerializedName("task_completion_rates")
    val taskCompletionRates: List<TaskCompletionRate>,
) {
    data class StudentProgress(
        val averageScore: Double,
        val completionPercentage: Double,
        val lastActivity: String,
        val userId: Int,
        val username: String,
    )

    data class TaskCompletionRate(
        val averageScore: Double,
        val completedBy: Int,
        val successRate: Double,
        val taskId: Int,
        val taskTitle: String,
    )
}
