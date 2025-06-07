package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.navigation.Screen
import hse.diploma.cybersecplatform.ui.screens.teacher.TeacherCoursesViewModel

@Composable
fun HomeScreenWrapper(
    viewModel: HomeViewModel = viewModel(factory = LocalViewModelFactory.current),
    teacherViewModel: TeacherCoursesViewModel = viewModel(factory = LocalViewModelFactory.current),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.allCoursesState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isTeacher by teacherViewModel.isTeacher.collectAsState()

    HomeScreen(
        state = state,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCourseClick = { courseId ->
            navController.navigate(Screen.TasksScreen.createRoute(courseId))
        },
        onReload = { viewModel.loadCourses() },
        onCreateCourseClick = {
            teacherViewModel.selectCourse(null)
            navController.navigate(Screen.CourseEditor.route)
        },
        isTeacher = isTeacher,
        modifier = modifier,
    )
}
