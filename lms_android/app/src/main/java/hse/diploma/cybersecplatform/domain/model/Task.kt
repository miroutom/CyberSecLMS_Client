package hse.diploma.cybersecplatform.domain.model

import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType

data class Task(
    val vulnerabilityType: VulnerabilityType,
    val number: Int,
    val description: String,
    val difficulty: Difficulty,
)
