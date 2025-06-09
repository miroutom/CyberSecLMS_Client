package hse.diploma.cybersecplatform.navigation

import androidx.annotation.StringRes
import hse.diploma.cybersecplatform.R

sealed class Screen(
    val route: String,
    @StringRes val titleId: Int? = null,
) {
    data object Onboarding : Screen("onboarding")

    data object Authorization : Screen("authorization")

    data object Registration : Screen("registration")

    data object HomeScreen : Screen("home_screen", R.string.homescreen_title)

    data object MyCourses : Screen("my_courses", R.string.my_courses_title)

    data object Profile : Screen("profile", R.string.profile_title)

    data object TasksScreen : Screen("tasksScreen/{courseId}", R.string.tasks_title) {
        fun createRoute(courseId: Int) = "tasksScreen/$courseId"
    }

    data object CodeEditor : Screen("code_editor/{courseId}/{taskId}", R.string.course_editor_title) {
        fun createRoute(
            courseId: Int,
            taskId: Int,
        ) = "code_editor/$courseId/$taskId"
    }

    data object Settings : Screen("settings", R.string.settings_title)

    data object CourseEditor : Screen("course_editor", R.string.course_editor_title)

    data object TaskEditor : Screen("task_editor/{taskId}") {
        fun createRoute(taskId: Int? = null) = "task_editor/${taskId ?: "new"}"
    }
}
