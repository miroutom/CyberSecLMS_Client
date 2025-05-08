package hse.diploma.cybersecplatform.domain.model

import hse.diploma.cybersecplatform.utils.Difficulty

data class Task(
    val vulnerabilityType: VulnerabilityType,
    val number: Int,
    val description: String,
    val difficulty: Difficulty,
)
