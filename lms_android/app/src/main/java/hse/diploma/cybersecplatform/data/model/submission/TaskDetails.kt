package hse.diploma.cybersecplatform.data.model.submission

data class TaskDetails(
    val id: Int,
    val title: String,
    val description: String,
    val initialCode: String,
    val targetCode: String?,
)
