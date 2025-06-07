package hse.diploma.cybersecplatform.data.model.request

data class UpdateTaskRequest(
    val title: String,
    val description: String,
    val type: String,
    val points: Int,
    val content: String,
)
