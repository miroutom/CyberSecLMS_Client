package hse.diploma.cybersecplatform.data.model.submission

import com.google.gson.annotations.SerializedName

data class TaskSubmissionDetails(
    @SerializedName("course_id")
    val courseId: Int,
    @SerializedName("course_name")
    val courseName: String,
    val feedback: String?,
    @SerializedName("graded_at")
    val gradedAt: String?,
    @SerializedName("max_score")
    val maxScore: Double,
    val score: Double?,
    val status: String,
    @SerializedName("submission_id")
    val submissionId: Int,
    @SerializedName("submitted_at")
    val submittedAt: String,
    @SerializedName("task_id")
    val taskId: Int,
    @SerializedName("task_title")
    val taskTitle: String,
)
