package hse.diploma.cybersecplatform.data.model.user

import com.google.gson.annotations.SerializedName

data class UserData(
    val id: Int,
    val username: String,
    val fullName: String,
    val email: String,
    @SerializedName("profileImage")
    val profileImage: String?,
    val isAdmin: Boolean = false,
    val isTeacher: Boolean = false,
    val isActive: Boolean = true,
    val lastLogin: String? = null,
    val courses: List<CourseProgress> = emptyList(),
    val totalTasks: Int = courses.sumOf { it.tasksCount },
    val completedTasks: Int = courses.sumOf { it.completedTasks },
    val progress: Double = if (totalTasks > 0) (completedTasks.toDouble() / totalTasks) * 100 else 0.0,
)
