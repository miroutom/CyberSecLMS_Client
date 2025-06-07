package hse.diploma.cybersecplatform.data.model.response

data class EvaluationResponse(
    val success: Boolean,
    val output: String,
    val errors: List<String>?,
)
