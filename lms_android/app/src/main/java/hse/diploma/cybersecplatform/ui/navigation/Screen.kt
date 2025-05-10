package hse.diploma.cybersecplatform.ui.navigation

import androidx.annotation.StringRes
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.VulnerabilityType

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

    data object TaskScreen : Screen("taskScreen/{vulnerabilityType}", R.string.tasks_title) {
        fun createRoute(vulnerabilityType: VulnerabilityType) = "taskScreen/${vulnerabilityType.name}"
    }
}
