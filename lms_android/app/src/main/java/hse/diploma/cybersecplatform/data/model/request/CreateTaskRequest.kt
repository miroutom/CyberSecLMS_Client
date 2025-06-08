package hse.diploma.cybersecplatform.data.model.request

data class CreateTaskRequest(
    val title: String,
    val description: String,
    val type: String,
    val points: Int,
    val content: String,
)
