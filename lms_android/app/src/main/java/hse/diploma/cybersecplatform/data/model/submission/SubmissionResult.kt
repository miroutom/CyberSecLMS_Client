package hse.diploma.cybersecplatform.data.model.submission

data class SubmissionResult(
    val success: Boolean,
    val score: Int? = null,
    val message: String? = null,
)
