package hse.diploma.cybersecplatform.domain.model

data class Course(
    val vulnerabilityType: VulnerabilityType,
    val completedTasks: Int = 0,
    val tasksCount: Int,
    val isStarted: Boolean = completedTasks > 0,
    val progress: Int = completedTasks * 100 / tasksCount,
)
