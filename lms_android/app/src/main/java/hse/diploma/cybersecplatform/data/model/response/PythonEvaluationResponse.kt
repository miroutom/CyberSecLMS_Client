package hse.diploma.cybersecplatform.data.model.response

data class PythonEvaluationResponse(
    val success: Boolean,
    val score: Int?,
    val output: String,
    val executionTime: Long,
)
