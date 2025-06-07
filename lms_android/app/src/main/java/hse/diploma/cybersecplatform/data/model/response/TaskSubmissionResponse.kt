package hse.diploma.cybersecplatform.data.model.response

import com.google.gson.annotations.SerializedName

data class TaskSubmissionResponse(
    val message: String,
    val status: String,
    @SerializedName("submission_id")
    val submissionId: Int,
    @SerializedName("submitted_at")
    val submittedAt: String,
    @SerializedName("task_id")
    val taskId: Int,
)
