package hse.diploma.cybersecplatform.domain.model

import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType

data class Task(
    val id: Int,
    val courseId: Int,
    val title: String,
    val description: String,
    val content: String,
    val vulnerabilityType: VulnerabilityType,
    val number: Int,
    val difficulty: Difficulty,
    val type: String,
    val points: Int,
    val isCompleted: Boolean = false,
)
