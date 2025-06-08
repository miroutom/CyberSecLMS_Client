package hse.diploma.cybersecplatform.data.model.submission

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TaskSubmission(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("task_id")
    val taskId: Int,
    @SerializedName("answer")
    val answer: String,
    @SerializedName("attachments")
    val attachments: List<String> = emptyList(),
    @SerializedName("submitted_at")
    val submittedAt: Date,
    @SerializedName("status")
    val status: String,
    @SerializedName("courseID")
    val courseId: Int,
)
