package hse.diploma.cybersecplatform.domain.model

data class Course(
    val vulnerabilityType: VulnerabilityType,
    val isStarted: Boolean = false,
    val completedTasks: Int = 0,
    val tasksCount: Int,
    val progress: Int = completedTasks * 100 / tasksCount,
)
