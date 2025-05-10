package hse.diploma.cybersecplatform.domain.model

data class Task(
    val vulnerabilityType: VulnerabilityType,
    val number: Int,
    val description: String,
    val difficulty: Difficulty,
)
