package hse.diploma.cybersecplatform.data.model.user

data class UserStats(
    val totalCourses: Int,
    val completedTasks: Int,
    val totalTasks: Int,
    val progress: Double,
)
