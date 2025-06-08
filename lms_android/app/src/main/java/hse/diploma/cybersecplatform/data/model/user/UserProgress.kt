package hse.diploma.cybersecplatform.data.model.user

data class UserProgress(
    val userId: Int,
    val completed: Map<Int, Boolean> = emptyMap(),
)
