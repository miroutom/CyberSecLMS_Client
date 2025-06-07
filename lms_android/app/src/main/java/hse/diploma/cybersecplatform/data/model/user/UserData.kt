package hse.diploma.cybersecplatform.data.model.user

data class UserData(
    val id: Int,
    val username: String,
    val fullName: String,
    val email: String,
    val profileImage: String? = null,
    val is2faEnabled: Boolean = true,
    val isActive: Boolean = true,
    val isAdmin: Boolean = false,
    val isTeacher: Boolean = false,
    val lastLogin: String? = null,
    val courses: List<CourseProgress>? = emptyList(),
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val progress: Double = 0.0,
)
