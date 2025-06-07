package hse.diploma.cybersecplatform.data.model.user

data class CourseProgress(
    val courseId: Int,
    val completedTasks: Int,
    val tasksCount: Int,
    val progress: Double = if (tasksCount > 0) (completedTasks.toDouble() / tasksCount) * 100 else 0.0,
)
