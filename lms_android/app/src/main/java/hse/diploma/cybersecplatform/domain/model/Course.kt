package hse.diploma.cybersecplatform.domain.model

import hse.diploma.cybersecplatform.ui.model.VulnerabilityType

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val vulnerabilityType: VulnerabilityType,
    val difficultyLevel: String,
    val category: String,
    val tasks: List<Task> = emptyList(),
    val completedTasks: Int = 0,
    val tasksCount: Int = tasks.size,
    val isStarted: Boolean = completedTasks > 0,
    val progress: Int = if (tasksCount > 0) (completedTasks * 100 / tasksCount) else 0,
) {
    fun isCompleted(): Boolean = progress >= 100
}
