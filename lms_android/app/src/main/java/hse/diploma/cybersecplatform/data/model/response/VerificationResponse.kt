package hse.diploma.cybersecplatform.data.model.response

data class VerificationResponse(
    val success: Boolean,
    val score: Int,
    val feedback: String,
)
