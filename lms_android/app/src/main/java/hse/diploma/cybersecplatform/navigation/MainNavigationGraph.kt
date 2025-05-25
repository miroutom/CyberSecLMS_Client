package hse.diploma.cybersecplatform.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.extensions.animatedComposable
import hse.diploma.cybersecplatform.ui.components.systemBars.AppScaffold
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.screens.courses.MyCoursesScreen
import hse.diploma.cybersecplatform.ui.screens.home.HomeScreen
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileScreen
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.screens.settings.SettingsScreen
import hse.diploma.cybersecplatform.ui.screens.tasks.TasksScreen

@Composable
fun MainNavigationGraph() {
    val mainNavController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel(factory = LocalViewModelFactory.current)

    AppScaffold(
        profileViewModel = profileViewModel,
        navController = mainNavController,
    ) { modifier ->
        NavHost(
            navController = mainNavController,
            startDestination = Screen.HomeScreen.route,
            modifier = modifier,
        ) {
            animatedComposable(Screen.HomeScreen.route) {
                HomeScreen(mainNavController)
            }
            animatedComposable(Screen.MyCourses.route) {
                MyCoursesScreen(mainNavController)
            }
            animatedComposable(Screen.Profile.route) {
                ProfileScreen(profileViewModel, mainNavController)
            }

            animatedComposable(Screen.TaskScreen.route) { backStackEntry ->
                val typeString = backStackEntry.arguments?.getString("vulnerabilityType")
                val type = VulnerabilityType.valueOf(typeString ?: "XSS")
                TasksScreen(type)
            }

            animatedComposable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
