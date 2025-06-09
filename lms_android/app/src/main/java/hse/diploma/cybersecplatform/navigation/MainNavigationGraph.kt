package hse.diploma.cybersecplatform.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.extensions.animatedComposable
import hse.diploma.cybersecplatform.ui.components.systemBars.AppScaffold
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.screens.code_editor.CodeEditorScreenWrapper
import hse.diploma.cybersecplatform.ui.screens.courses.MyCoursesScreenWrapper
import hse.diploma.cybersecplatform.ui.screens.home.HomeScreenWrapper
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileScreenWrapper
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.screens.settings.SettingsScreenWrapper
import hse.diploma.cybersecplatform.ui.screens.tasks.TasksScreenWrapper
import hse.diploma.cybersecplatform.ui.screens.teacher.CourseEditorScreenWrapper
import hse.diploma.cybersecplatform.ui.screens.teacher.TaskEditorScreenWrapper
import hse.diploma.cybersecplatform.utils.logD

@Composable
fun MainNavigationGraph(authStateViewModel: AuthStateViewModel) {
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
                HomeScreenWrapper(navController = mainNavController)
            }
            animatedComposable(Screen.MyCourses.route) {
                MyCoursesScreenWrapper(navController = mainNavController)
            }
            animatedComposable(Screen.Profile.route) {
                ProfileScreenWrapper(profileViewModel, authStateViewModel, mainNavController)
            }

            animatedComposable(Screen.TasksScreen.route) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId")?.toIntOrNull()
                if (courseId != null) {
                    TasksScreenWrapper(
                        courseId = courseId,
                        navController = mainNavController,
                    )
                }
            }

            animatedComposable(Screen.CodeEditor.route) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId")?.toIntOrNull()
                val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
                logD("CodeEditorScreen", "courseId: $courseId, taskId: $taskId")
                if (courseId != null && taskId != null) {
                    CodeEditorScreenWrapper(courseId, taskId)
                }
            }

            animatedComposable(Screen.Settings.route) {
                SettingsScreenWrapper(authStateViewModel = authStateViewModel)
            }

            animatedComposable(Screen.CourseEditor.route) {
                CourseEditorScreenWrapper(navController = mainNavController)
            }

            animatedComposable(Screen.TaskEditor.route) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")
                TaskEditorScreenWrapper(
                    navController = mainNavController,
                    taskId = taskId?.toIntOrNull(),
                )
            }
        }
    }
}
