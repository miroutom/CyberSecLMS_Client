package hse.diploma.cybersecplatform.domain.model

import com.google.gson.annotations.SerializedName

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("vulnerabilityType")
    val vulnerabilityType: String,
    @SerializedName("difficulty_level")
    val difficultyLevel: String,
    val category: String,
    @SerializedName("tasks")
    val tasks: List<Task> = emptyList(),
    @SerializedName("completed_tasks")
    val completedTasks: Int = tasks.count { it.isCompleted },
    val tasksCount: Int = tasks.size,
    @SerializedName("is_started")
    val isStarted: Boolean = completedTasks > 0,
    val progress: Int = if (tasksCount > 0) (completedTasks * 100 / tasksCount) else 0,
) {
    fun isCompleted(): Boolean = tasksCount > 0 && progress == 100

    fun updateWithTasks(updatedTasks: List<Task>): Course {
        return this.copy(
            tasks = updatedTasks,
            completedTasks = updatedTasks.count { it.isCompleted },
        )
    }
}
